# AWS Code 시리즈 정복기

### CodeCommit (Github)

### CodeBuild (코드 ➔ Jar로 Build - Jenkins)

### CodeDeploy (EC2에 배포)

> ### Step1. Github + AWS Code 시리즈로 CI/CD 구성
>
> [CodePipeline]
>
> `GitHub` ➔ `AWS CodeBuild` ➔ `AWS CodeDeploy` ➔ `AWS EC2`

## 1. 역할 생성

#### Code Build (CodeBuild-Role)

AmazonS3FullAccess (S3에 빌드 저장물(JAR)를 저장해야해서), AWSCodeBuildAdminAccess, CloudWatchLogsFullAccess (빌드간 로그 남기기 위해)

#### Code Deploy (CodeDeploy-Role)

AWSCodeDeployRole

#### EC2 (EC2-CodeDeploy-Role)

AmazonEC2RoleforAWSCodeDeploy, AmazonS3ReadOnlyAccess (S3에서 빌드 저장물(JAR) 불러오려고)

## 2. 대상 EC2 생성

AmazonSSMManagedInstanceCore - SSM 연결시 EC2 Role에 추가해주고 재부팅\
https://docs.aws.amazon.com/ko_kr/codedeploy/latest/userguide/codedeploy-agent-operations-install-linux.html

CodeDeploy Agent EC2 내에 설치

wget https://aws-codedeploy-ap-northeast-2.s3.ap-northeast-2.amazonaws.com/latest/install
