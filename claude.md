# TOOK-SCAN Backend 프로젝트 정보

## 프로젝트 개요
- **프로젝트 명**: TOOK-SCAN Backend
- **기술 스택**: Java 17, Spring Boot 3.3.5, MySQL, Redis, AWS
- **아키텍처**: Clean Architecture (Domain-Driven Design)
- **빌드 도구**: Gradle
- **패키지 구조**: `com.tookscan.tookscan`

## 프로젝트 구조

### 주요 디렉토리
```
src/main/java/com/tookscan/tookscan/
├── account/           # 계정 관리 (사용자, 관리자, 그룹)
├── address/           # 주소 관리
├── core/             # 공통 모듈 (설정, 유틸리티, 예외처리)
├── mail/             # 메일 서비스
├── message/          # 메시지 처리
├── order/            # 주문 처리 (핵심 비즈니스 로직)
├── payment/          # 결제 처리
├── security/         # 인증/인가 (JWT, OAuth2)
└── term/             # 약관 관리
```

### 아키텍처 패턴
각 도메인은 다음과 같은 계층 구조를 따릅니다:
- **presentation/**: 컨트롤러, DTO
- **application/**: 서비스, 유스케이스
- **domain/**: 도메인 모델, 도메인 서비스
- **repository/**: 데이터 접근 계층 (JPA, Redis)

## 주요 기능

### 1. 인증/인가 (security/)
- JWT 기반 토큰 인증
- OAuth2 (카카오, 구글, 네이버)
- 다중 사용자 역할 (USER, ADMIN)
- 비밀번호 변경, 계정 삭제 기능

### 2. 주문 관리 (order/)
- 문서 스캔 주문 생성/관리
- 배송 상태 추적
- 쿠폰 적용 시스템
- OCR 스캔 서비스 연동
- 문서 PDF 생성/업로드

### 3. 결제 처리 (payment/)
- Toss Payments 연동
- 결제 확인 및 환불 처리
- 다양한 결제 방법 지원

### 4. 계정 관리 (account/)
- 사용자/관리자 계정 관리
- 그룹 기반 권한 관리
- 사용자 정보 CRUD

## 주요 기술 스택 및 의존성

### Spring Boot 관련
- Spring Boot 3.3.5
- Spring Security (OAuth2 포함)
- Spring Data JPA
- Spring Data Redis
- Spring Mail
- Spring Validation

### 데이터베이스
- MySQL (운영 DB)
- Redis (캐시, 세션 관리)
- QueryDSL (동적 쿼리)

### 외부 서비스 연동
- **AWS**: S3 (파일 저장), CloudFront (CDN)
- **Toss Payments**: 결제 처리
- **OAuth2**: 소셜 로그인 (카카오, 구글, 네이버)
- **Solapi**: SMS 발송
- **Slack**: 에러 알림
- **DeliveryTracker**: 배송 추적

### 개발 도구
- Lombok: 코드 간소화
- Swagger/OpenAPI: API 문서화
- Apache POI: Excel 처리
- JWT: 토큰 기반 인증

## 환경 설정

### 필수 환경 변수
- 데이터베이스: `SPRING_DATASOURCE_URL`, `SPRING_DATASOURCE_USERNAME`, `SPRING_DATASOURCE_PASSWORD`
- Redis: `SPRING_REDIS_HOST`, `SPRING_REDIS_PORT`
- JWT: `JWT_SECRET_KEY`, `JWT_ACCESS_TOKEN_EXPIRE_PERIOD`
- AWS: `AWS_ACCESS_KEY`, `AWS_SECRET_KEY`, `S3_BUCKET`
- OAuth2: 각 provider별 클라이언트 ID/Secret
- 외부 서비스: Toss Payments, Solapi, Slack Webhook 등

### 로컬 개발 환경
```bash
# Docker Compose로 로컬 서비스 실행
docker-compose -f docker-compose.local.yml up --build -d

# 또는 로컬 스크립트 실행
./run-local.sh
```

## 테스트 실행
```bash
# 전체 테스트
./gradlew test

# 특정 테스트
./gradlew test --tests "com.tookscan.tookscan.ClassName"
```

## 빌드 및 배포
```bash
# 빌드
./gradlew build

# JAR 파일 생성 (application.jar)
./gradlew bootJar
```

## API 문서
- Swagger UI: `/swagger-ui.html`
- API 문서: `/v3/api-docs`

## 주요 특징
1. **Clean Architecture**: 도메인 중심 설계
2. **멀티 테넌시**: 사용자/관리자 역할 분리
3. **이벤트 기반**: 이메일, SMS 등 비동기 처리
4. **보안 강화**: JWT + OAuth2 + 암호화
5. **모니터링**: Slack 알림, Health Check
6. **성능 최적화**: Redis 캐시, QueryDSL, 배치 처리

## 개발 규칙
- 커밋 컨벤션: `type/#issue-number: description`
- 코드 스타일: 기존 코드 패턴 준수
- 테스트: 새로운 기능 추가 시 테스트 코드 작성 필수