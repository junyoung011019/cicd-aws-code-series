# Step1. Github + AWS Code 시리즈로 CI/CD 구성

> [CodePipeline]
>
> `GitHub` ➔ `AWS CodeBuild` ➔ `AWS CodeDeploy` ➔ `AWS EC2`

---

## 1. 역할 생성

#### Code Build (CodeBuild-Role)

| 정책 | 이유 |
|------|------|
| `AmazonS3FullAccess` | S3에 빌드 저장물(JAR)를 저장해야해서 |
| `AWSCodeBuildAdminAccess` | CodeBuild 권한 |
| `CloudWatchLogsFullAccess` | 빌드간 로그 남기기 위해 |

#### Code Deploy (CodeDeploy-Role)

| 정책 | 이유 |
|------|------|
| `AWSCodeDeployRole` | EC2 배포 명령 |

#### EC2 (EC2-CodeDeploy-Role)

| 정책 | 이유 |
|------|------|
| `AmazonEC2RoleforAWSCodeDeploy` | CodeDeploy 연동 |
| `AmazonS3ReadOnlyAccess` | S3에서 빌드 저장물(JAR) 불러오려고 |
| `AmazonSSMManagedInstanceCore` | SSM 연결시 EC2 Role에 추가해주고 재부팅 |
| `CloudWatchAgentServerPolicy` | CloudWatch 로그 |

---

## 2. 대상 EC2 생성

- AMI: Amazon Linux 2023
- 인스턴스 타입: t3.micro
- IAM Role: `EC2-CodeDeploy-Role`
- 보안 그룹: SSH(22), HTTP(80), 8080 인바운드 오픈
- 태그: `Name = cicd-server`

#### CodeDeploy Agent 설치

> 참고: [CodeDeploy Agent 설치 가이드](https://docs.aws.amazon.com/ko_kr/codedeploy/latest/userguide/codedeploy-agent-operations-install-linux.html)

```bash
sudo yum install -y ruby wget
wget https://aws-codedeploy-ap-northeast-2.s3.ap-northeast-2.amazonaws.com/latest/install
chmod +x ./install
sudo ./install auto
sudo systemctl enable --now codedeploy-agent
```

---

## 3. Github Repo 생성 및 연동

```bash
git init
git remote add origin https://github.com/<username>/cicd-aws-code-series.git
git push -u origin main
```

---

## 4. 설정 파일 구성 (buildspec.yml / appspec.yml / scripts/deploy.sh)

### [buildspec.yml (빌드 명세서)](https://docs.aws.amazon.com/ko_kr/codebuild/latest/userguide/build-spec-ref.html#build-spec-ref-syntax)

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
    - '**/*.jar'
  base-directory: build/libs
```

### [appspec.yml (배포 명세서)](https://docs.aws.amazon.com/ko_kr/codedeploy/latest/userguide/reference-appspec-file.html)

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

### deploy.sh (Spring 재실행 배포 스크립트)

```bash
#!/bin/bash

# 1. 로그 파일 경로 변수로 잡기
LOG_FILE=/home/ec2-user/spring/logs/app-$(TZ=Asia/Seoul date +%Y%m%d).log

# 2. logs 폴더 생성
mkdir -p /home/ec2-user/spring/logs

# 3. 배포 시작 시간 기록
echo "===== 배포 시작: $(TZ=Asia/Seoul date '+%Y-%m-%d %H:%M:%S') =====" >> $LOG_FILE

# 4. 기존 프로세스 있다면 종료
if pgrep -f '*.jar' > /dev/null; then
    kill -9 $(pgrep -f '*.jar')
fi

# 5. Spring 프로세스 기동
nohup java -jar /home/ec2-user/spring/*.jar >> $LOG_FILE 2>&1 &
```

---

## 5. CodeBuild 프로젝트 생성

> 작성 예정

## 6. CodeDeploy 애플리케이션 + 배포 그룹 생성

> 작성 예정

## 7. CodePipeline 생성

> 작성 예정
