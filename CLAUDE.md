# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Purpose

This is a Spring Boot web application used as a teaching vehicle for building CI/CD pipelines on AWS. The application logic is intentionally minimal — the real work is in the infrastructure files students create at each stage.

## Build & Run Commands

All commands use the Gradle wrapper. On Windows use `gradlew.bat`; on Unix use `./gradlew`.

```
gradlew.bat build        # Compile and package JAR
gradlew.bat bootRun      # Run the application locally (http://localhost:8080)
gradlew.bat test         # Run all tests
gradlew.bat clean build  # Full clean rebuild
```

Run a single test class:
```
gradlew.bat test --tests "com.example.cicd_aws_code_series.CicdAwsCodeSeriesApplicationTests"
```

- Java 21, Spring Boot 4.0.6, Gradle 9.4.1
- Built JAR lands in `build/libs/`

## Architecture

The application is a minimal Spring MVC app with one controller that displays a version string. Its purpose is to give CI/CD pipelines something concrete to build and deploy.

**The 4 files that matter for the CI/CD pipeline:**

| File | Location | Role |
|------|----------|------|
| `HelloController.java` | `src/main/java/.../` | Displays version text — change this to verify a deployment worked |
| `buildspec.yml` | project root | AWS CodeBuild spec: how to compile the JAR |
| `appspec.yml` | project root | AWS CodeDeploy spec: where to put files and what scripts to run on EC2 |
| `scripts/deploy.sh` | `scripts/` | Shell script run on EC2 to stop the old process and start the new JAR |

> `appspec.yml` and `deploy.sh` are written once in Stage 1 and reused unchanged in Stage 2.

## CI/CD Learning Roadmap (2 Stages)

> CodeCommit is unavailable on free-tier AWS accounts (restricted since 2024). Roadmap starts from GitHub directly.

**Stage 1 — GitHub + AWS:**  
`Local` → `GitHub` → `CodeBuild` → `CodeDeploy` → `EC2` (orchestrated by CodePipeline)

**Stage 2 — Jenkins as orchestrator:**  
`Local` → `GitHub` → `Jenkins` → `CodeDeploy` → `EC2`  
Drops CodeBuild and CodePipeline; Jenkins builds the JAR, uploads to S3, then triggers CodeDeploy.

## AWS Infrastructure (Seoul / ap-northeast-2)

| Resource | Name | Notes |
|----------|------|-------|
| EC2 | `cicd-server` | t3.micro, Amazon Linux 2023 |
| IAM Role (EC2) | `EC2-CodeDeploy-Role` | AmazonEC2RoleforAWSCodeDeploy, AmazonS3ReadOnlyAccess, AmazonSSMManagedInstanceCore, CloudWatchAgentServerPolicy |
| IAM Role (CodeBuild) | `CodeBuild-Role` | AmazonS3FullAccess, AWSCodeBuildAdminAccess, CloudWatchLogsFullAccess |
| IAM Role (CodeDeploy) | `CodeDeploy-Role` | AWSCodeDeployRole |

## Current Progress

- [x] IAM Roles 생성 완료
- [x] EC2 생성 완료
- [x] CodeDeploy Agent 설치 완료
- [x] GitHub 레포 생성 완료
- [ ] buildspec.yml, appspec.yml, scripts/deploy.sh 작성
- [ ] CodeBuild 프로젝트 생성
- [ ] CodeDeploy 애플리케이션 + 배포 그룹 생성
- [ ] CodePipeline 생성
