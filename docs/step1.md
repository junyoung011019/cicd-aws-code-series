# Step 1. AWS Native CI/CD 구성

> [CodePipeline]
>
> `CodeCommit` ➔ `AWS CodeBuild` ➔ `AWS CodeDeploy` ➔ `AWS EC2`

---

## 1. IAM 역할 생성

### CodeBuild-Role

| 정책 | 이유 |
|------|------|
| `AmazonS3FullAccess` | S3에 빌드 아티팩트(JAR) 저장 |
| `AWSCodeBuildAdminAccess` | CodeBuild 실행 권한 |
| `AWSCodeCommitReadOnly` | CodeCommit 소스 읽기 |
| `CloudWatchLogsFullAccess` | 빌드 로그 기록 |

### CodeDeploy-Role

| 정책 | 이유 |
|------|------|
| `AWSCodeDeployRole` | EC2 배포 명령 |

### EC2-CodeDeploy-Role

| 정책 | 이유 |
|------|------|
| `AmazonEC2RoleforAWSCodeDeploy` | CodeDeploy 연동 |
| `AmazonS3ReadOnlyAccess` | S3에서 빌드 아티팩트 다운로드 |
| `AmazonSSMManagedInstanceCore` | SSM 연결 |
| `CloudWatchAgentServerPolicy` | CloudWatch 로그 |

---

## 2. EC2 생성

- AMI: Amazon Linux 2023
- 인스턴스 타입: t3.micro
- 리전: us-east-1
- IAM Role: `EC2-CodeDeploy-Role`
- 보안 그룹: SSH(22), 8080 인바운드 오픈
- 태그: `Name = final-server`

### CodeDeploy Agent 설치

> 참고: [CodeDeploy Agent 설치 가이드](https://docs.aws.amazon.com/ko_kr/codedeploy/latest/userguide/codedeploy-agent-operations-install-linux.html)

```bash
sudo yum install -y ruby wget
wget https://aws-codedeploy-us-east-1.s3.us-east-1.amazonaws.com/latest/install
chmod +x ./install
sudo ./install auto
sudo systemctl enable --now codedeploy-agent
```

---

## 3. CodeCommit 레포 생성 및 SSH 연동

### SSH 키 생성 및 등록

```bash
ssh-keygen -t rsa -b 4096 -f ~/.ssh/codecommit_rsa
```

생성된 공개키(`codecommit_rsa.pub`)를 IAM → 보안 자격증명 → SSH 퍼블릭 키에 등록.

### SSH config 설정

`~/.ssh/config` 파일에 추가:

```
Host git-codecommit.*.amazonaws.com
  User <IAM에서 발급된 SSH 키 ID>
  IdentityFile ~/.ssh/codecommit_rsa
```

### remote 추가

```bash
git remote add codecommit ssh://git-codecommit.us-east-1.amazonaws.com/v1/repos/cicd-aws-code-series
git push codecommit main
```

---

## 4. 설정 파일 구성

### buildspec.yml

```yaml
version: 0.2

env:
  variables:
    APP_VERSION: "1.0.0"

phases:
  install:
    runtime-versions:
      java: corretto21 # https://docs.aws.amazon.com/ko_kr/codebuild/latest/userguide/available-runtimes.html
  build:
    commands:
      - chmod +x ./gradlew
      - ./gradlew build

artifacts:
  files:
    - build/libs/*.jar
    - appspec.yml
    - scripts/**
  discard-paths: no
  packaging: ZIP
  name: spring-service.zip
```

### appspec.yml

```yaml
version: 0.0
os: linux
files:
  - source: /          # 어떤 파일을 들고올껀지
    destination: /home/ec2-user/spring  # 어디 위치에 풀껀지
hooks:
  ApplicationStart:
    - location: scripts/deploy.sh
      timeout: 60
```

### scripts/deploy.sh

```bash
#!/bin/bash

# 1. 로그 파일 경로 변수로 잡기
LOG_FILE=/home/ec2-user/spring/logs/app-$(TZ=Asia/Seoul date +%Y%m%d).log

# 2. logs 폴더 생성
mkdir -p /home/ec2-user/spring/logs

# 3. 배포 시작 시간 기록
echo "===== 배포 시작: $(TZ=Asia/Seoul date '+%Y-%m-%d %H:%M:%S') =====" >> $LOG_FILE

# 4. Java 설치 (없을 경우)
if ! command -v java &> /dev/null; then
    sudo yum install -y java-21-amazon-corretto
fi

# 5. 기존 프로세스 있다면 종료
if pgrep -f 'java' > /dev/null; then
    kill -9 $(pgrep -f 'java')
fi

# 6. Spring 프로세스 기동
nohup java -jar /home/ec2-user/spring/build/libs/*[^plain].jar >> $LOG_FILE 2>&1 &
```

---

## 5. CodeBuild 프로젝트 생성

- 리전: us-east-1
- 소스: AWS CodeCommit - `cicd-aws-code-series` (main 브랜치)
- 환경: Amazon Linux / 표준 / 최신 이미지 / 서비스 역할: `CodeBuild-Role`
- Buildspec: 리포지토리의 buildspec.yml 사용
- 아티팩트: S3 버킷 지정, 패키징 ZIP

---

## 6. CodeDeploy 애플리케이션 + 배포 그룹 생성

- 애플리케이션 이름: `cicd-app`
- 컴퓨팅 플랫폼: EC2/온프레미스
- 배포 그룹 이름: `cicd-deploy-group`
- 서비스 역할: `CodeDeploy-Role`
- 배포 유형: 현재 위치
- 환경 구성: Amazon EC2 인스턴스 - 태그 `Name = final-server`
- 배포 설정: CodeDeployDefault.AllAtOnce
- 로드 밸런서: 비활성화

---

## 7. CodePipeline 생성

![CodePipeline](step1_code%20pipeline.png)

- 리전: us-east-1
- 파이프라인 이름: `cicd-pipeline`
- 파이프라인 유형: V2 / 실행 모드: QUEUED

### Source 스테이지
- 공급자: AWS CodeCommit
- 리포지토리: `cicd-aws-code-series` / 브랜치: `main`
- 변경 감지: Amazon CloudWatch Events

### Build 스테이지
- 공급자: AWS CodeBuild
- 프로젝트: 위에서 생성한 CodeBuild 프로젝트 선택

### Deploy 스테이지
- 공급자: AWS CodeDeploy
- 애플리케이션: `cicd-app`
- 배포 그룹: `cicd-deploy-group`
