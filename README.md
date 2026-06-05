# 🚀 AWS Code 시리즈 정복기

AWS Code 시리즈(CodeCommit, CodeBuild, CodeDeploy, CodePipeline)와 Jenkins를 활용한 CI/CD 파이프라인을 단계별로 구축하며 인프라 구조를 학습하는 프로젝트입니다.

---

## 🗺️ 로드맵

| 단계 | 흐름 |
|------|------|
| [Step 1. AWS Native](docs/step1.md) | `CodeCommit` → `CodeBuild` → `CodeDeploy` → `EC2` |
| [Step 2. GitHub 연동](docs/step2.md) | `GitHub` → `CodeBuild` → `CodeDeploy` → `EC2` |
| [Step 3. Jenkins 연동](docs/step3.md) | `GitHub` → `Jenkins` → `CodeDeploy` → `EC2` |

---

## 📂 핵심 파일

| 파일 | 역할 | 사용 단계 |
|------|------|---------|
| `buildspec.yml` | CodeBuild 빌드 명세서 | Step 1, 2 |
| `appspec.yml` | CodeDeploy 배포 명세서 | 전 단계 |
| `scripts/deploy.sh` | EC2 배포 스크립트 | 전 단계 |

---

## ⚙️ 기술 스택

- AWS CodeCommit, CodeBuild, CodeDeploy, CodePipeline
- Jenkins
- GitHub
- Java 21 / Spring Boot / Gradle (배포 대상 앱)
