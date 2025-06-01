# Backend
TOOK-SCAN 백엔드 레포지토리입니다.

## 로컬 실행 방법

### 1. 사전 준비

- Java 17 이상이 설치되어 있어야 합니다.
- Docker & Docker Compose가 설치되어 있어야 합니다.
- MySQL, Redis 등 필요한 서비스는 Docker Compose로 함께 실행합니다.

### 2. 환경변수 파일 생성

프로젝트 루트에 `.env.local` 파일을 생성하고, 환경변수들을 정의합니다.
노션에 환경변수 내용이 있으니, 해당 문서를 참고하여 필요한 환경변수를 설정하세요.

- [노션 링크](https://www.notion.so/env-local-20568122bf4180909c4cf12f0b322056?source=copy_link)

### 3.Docker Compose로 로컬 서비스 실행

루트 디렉터리에 docker-compose.local.yml 파일을 두고, 터미널에서 아래 명령어를 실행하세요.
이 명령은 MySQL, Redis, 백엔드 컨테이너를 한꺼번에 띄웁니다.

```bash
docker-compose -f docker-compose.local.yml up --build -d
```

# Commit Convention
| Type | 설명 | 예시                                               |
| --- | --- |--------------------------------------------------|
| fix | 버그, 오류 해결 | ex) fix/#10: callback error                      |
| add | Feat 이외의 부수적인 코드 추가/ 라이브러리 추가/ 새로운 View나 Activity 생성 | ex) add/#11: LoginActivity                       |
| feat | 새로운 기능 구현 | ex) feat/#11: google login                       |
| del | 쓸모없는 코드 삭제 | ex) del/#12: unnecessary import package          |
| remove | 파일 삭제 | ex) remove/#12: 중복 파일 삭제                         |
| refactor | 내부 로직은 변경 하지 않고 기존의 코드 개선하는 리팩토링 시, 세미콜론 줄바꿈 포함 | ex) refactor/#15: MVP architecture to MVVM       |
| chore | 그 이외의 잡일/ 버전 코드 수정, 패키지 구조 변경, 파일 이동, 가독성이나 변수명, reformat 등 | ex) chore/#20: delete unnecessary import package |
| comment | 필요한 주석 추가 및 변경 | ex) comment/#30: 메인 뷰컨 주석 추가                     |
| docs | README나 wiki 등 내용 추가 및 변경 | ex) docs/#30: README 내용 추가                       |
| test | 테스트 코드 추가 | ex) TEST/#30: 로그인 토큰 테스트 코드 추가                   |