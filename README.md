# 🚀 AWS Code 시리즈 정복기

AWS Code 시리즈(CodeBuild, CodeDeploy, CodePipeline)와 Jenkins를 활용한 CI/CD 파이프라인을 단계별로 구축하며 인프라 구조를 학습하는 프로젝트입니다.

### 1. CodeCommit (Github)

### 2. CodeBuild (코드 ➔ Jar로 Build - Jenkins)

### 3. CodeDeploy (EC2에 배포)

---

## 🗺️ 로드맵

| 단계                   | 흐름                                            |
| -------------------- | --------------------------------------------- |
| [Step 1. GitHub + AWS](docs/step1.md) | `GitHub` → `CodeBuild` → `CodeDeploy` → `EC2` |
| [Step 2. Jenkins](docs/step2.md)      | `GitHub` → `Jenkins` → `CodeDeploy` → `EC2`   |

---

## 📂 핵심 파일

| 파일                             | 역할                |
| ------------------------------ | ----------------- |
| `buildspec.yml`                | CodeBuild 빌드 명세서  |
| `appspec.yml`                  | CodeDeploy 배포 명세서 |
| `scripts/deploy.sh`            | EC2 배포 스크립트       |


---

## ⚙️ 기술 스택

- AWS CodeBuild, CodeDeploy, CodePipeline
- Jenkins
- GitHub
- Java 21 / Spring Boot / Gradle (배포 대상 앱)
