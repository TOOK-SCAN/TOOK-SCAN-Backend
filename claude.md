# TOOK-SCAN Backend 개발 가이드

## 프로젝트 개요
- **프로젝트**: TOOK-SCAN Backend (문서 스캔 서비스)
- **기술 스택**: Java 17, Spring Boot 3.3.5, MySQL, Redis, AWS
- **아키텍처**: Clean Architecture (Domain-Driven Design)
- **빌드 도구**: Gradle

## 프로젝트 구조
```
src/main/java/com.tookscan.tookscan/
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

각 도메인의 계층 구조:
- **presentation/**: 컨트롤러, DTO
- **application/**: 서비스, 유스케이스
- **domain/**: 도메인 모델, 도메인 서비스
- **repository/**: 데이터 접근 계층 (JPA, Redis)

## 핵심 기술 스택
- **Spring Boot**: 3.3.5 (Security, Data JPA, Data Redis, Mail, Validation)
- **데이터베이스**: MySQL (운영), Redis (캐시), QueryDSL (동적 쿼리)
- **외부 서비스**: AWS S3/CloudFront, Toss Payments, OAuth2 (카카오/구글/네이버), Solapi SMS, Slack
- **개발 도구**: Lombok, Swagger/OpenAPI, Apache POI, JWT

## 환경 설정
### 필수 환경 변수
```bash
# 데이터베이스
SPRING_DATASOURCE_URL, SPRING_DATASOURCE_USERNAME, SPRING_DATASOURCE_PASSWORD
SPRING_REDIS_HOST, SPRING_REDIS_PORT

# 보안
JWT_SECRET_KEY, JWT_ACCESS_TOKEN_EXPIRE_PERIOD

# AWS
AWS_ACCESS_KEY, AWS_SECRET_KEY, S3_BUCKET

# 외부 서비스
# OAuth2 각 provider별 클라이언트 ID/Secret
# Toss Payments, Solapi, Slack Webhook 등
```

### 로컬 개발 환경
```bash
# Docker Compose 실행
docker-compose -f docker-compose.local.yml up --build -d

# 또는 로컬 스크립트 실행
./run-local.sh
```

### 빌드 및 테스트
```bash
# 빌드
./gradlew build

# 테스트
./gradlew test

# JAR 파일 생성
./gradlew bootJar
```

## API 개발 표준 구조

### 1. 기본 구조
하나의 API는 다음 구조를 따릅니다:
- **Controller**: API 엔드포인트 정의
- **UseCase**: 비즈니스 로직 인터페이스
- **Service**: UseCase 구현체 (비즈니스 로직 실행)
- **RequestDto**: 요청 데이터 (필요시)
- **ResponseDto**: 응답 데이터 (필요시, Void 가능)

### 2. Controller 구조
```
presentation/controller/
├── command/                    # 조회가 아닌 API (생성, 수정, 삭제)
│   ├── {Domain}AdminCommandV1Controller.java
│   └── {Domain}UserCommandV1Controller.java
└── query/                      # 조회 API
    ├── {Domain}AdminQueryV1Controller.java
    └── {Domain}UserQueryV1Controller.java
```

**분류 기준**:
- **Query**: 조회 API → `query/` 폴더
- **Command**: 생성/수정/삭제 API → `command/` 폴더
- **Admin**: 관리자 API → `{Domain}AdminXXXController`
- **User**: 사용자 API → `{Domain}UserXXXController`

**Controller 구현 예시**:
```java
@RestController
@RequestMapping("/v1/admins")
@RequiredArgsConstructor
@Tag(name = "Order", description = "주문 관련 API")
public class OrderAdminCommandV1Controller {
    
    private final UpdateAdminOrderStatusUseCase updateAdminOrderStatusUseCase;
    
    @Operation(summary = "주문 상태 업데이트", description = "관리자가 주문 상태를 업데이트합니다.")
    @PatchMapping("/orders/{orderId}/status")
    public ResponseEntity<Void> updateOrderStatus(
            @AccountID UUID accountId,
            @PathVariable Long orderId,
            @RequestBody @Valid UpdateAdminOrderStatusRequestDto requestDto) {
        updateAdminOrderStatusUseCase.execute(accountId, orderId, requestDto);
        return ResponseEntity.ok().build();
    }
}
```

### 3. UseCase 및 Service 구조
```
application/
├── usecase/
│   └── {ActionName}UseCase.java      # 인터페이스
└── service/
    └── {ActionName}Service.java      # 구현체
```

**UseCase 인터페이스**:
```java
@UseCase
public interface UpdateAdminOrderStatusUseCase {
    void execute(UUID accountId, Long orderId, UpdateAdminOrderStatusRequestDto requestDto);
}
```

**Service 구현체**:
```java
@Service
@RequiredArgsConstructor
public class UpdateAdminOrderStatusService implements UpdateAdminOrderStatusUseCase {
    
    private final OrderRepository orderRepository;
    private final OrderService orderService;
    
    @Override
    @Transactional
    public void execute(UUID accountId, Long orderId, UpdateAdminOrderStatusRequestDto requestDto) {
        Order order = orderRepository.findByIdOrElseThrow(orderId);
        orderService.updateStatus(order, requestDto.status());
        orderRepository.save(order);
    }
}
```

### 4. DTO 구조
```
presentation/dto/
├── request/
│   └── {ActionName}RequestDto.java
└── response/
    └── {ActionName}ResponseDto.java
```

**RequestDto (Record 타입)**:
```java
public record UpdateAdminOrderStatusRequestDto(
    @NotNull EOrderStatus status,
    @Size(max = 500) String reason
) {}
```

**ResponseDto (클래스 타입)**:
```java
@Getter
public class ReadAdminOrderDetailResponseDto extends SelfValidating<ReadAdminOrderDetailResponseDto> {
    
    @JsonProperty("order_id")
    private final Long orderId;
    
    @JsonProperty("order_status")
    private final EOrderStatus orderStatus;
    
    public static ReadAdminOrderDetailResponseDto fromEntity(Order order) {
        return new ReadAdminOrderDetailResponseDto(order.getId(), order.getOrderStatus());
    }
}
```

### 5. Repository 구조
```
repository/
├── {Domain}Repository.java           # 인터페이스
├── impl/
│   └── {Domain}RepositoryImpl.java    # 구현체
└── mysql/
    └── {Domain}JpaRepository.java     # JPA Repository
```

**특징**:
- 인터페이스 분리로 도메인 로직과 구현 기술 분리
- JPA + QueryDSL 활용 (기본 CRUD는 JPA, 복잡한 쿼리는 QueryDSL)
- 커스텀 예외로 일관된 에러 처리

### 6. Domain Service 구조
```
domain/service/
└── {Domain}Service.java
```

**특징**:
- Repository 의존성 없이 순수 비즈니스 로직만 담당
- 도메인 규칙과 유효성 검사 관리
- 엔티티 상태 변경 규칙 관리

```java
@Service
@RequiredArgsConstructor
public class OrderService {
    
    public void updateStatus(Order order, EOrderStatus newStatus) {
        validateStatusTransition(order.getOrderStatus(), newStatus);
        order.updateStatus(newStatus);
    }
    
    private void validateStatusTransition(EOrderStatus current, EOrderStatus target) {
        // 상태 전환 규칙 검증
    }
}
```

### 7. 네이밍 컨벤션
- **Controller**: `{Domain}{User/Admin}{Command/Query}V1Controller`
- **UseCase**: `{Action}{User/Admin}{Domain}UseCase`
- **Service**: `{Action}{User/Admin}{Domain}Service`
- **RequestDto**: `{Action}{User/Admin}{Domain}RequestDto`
- **ResponseDto**: `{Action}{User/Admin}{Domain}ResponseDto`

**일관성 규칙**: 하나의 API에 대해 UseCase, Service, RequestDto, ResponseDto는 접미사를 제외하고 동일한 네이밍 사용

### 8. 조회 API 응답 레벨
- **Brief**: 최소 정보 (ID, 이름 등) - 선택 옵션용
- **Summary**: 주요 필드 + 메타데이터 - 간단한 정보 표시
- **Overview**: 주요 필드 + 연관 데이터 요약 - 대시보드용
- **Detail**: 모든 정보 - 상세 페이지용

## 개발 패턴

### 인증/인가
```java
// JWT 토큰 구조
- Access Token: 쿠키 저장 (짧은 시간)
- Refresh Token: Redis 저장 (14일)
- 토큰 클레임: aid (Account ID), rol (Role)

// URL 패턴별 권한 설정
- 관리자 API: "/v1/admins/**" → ADMIN 권한
- 사용자 API: "/v1/users/**" → USER 권한
- 공통 API: "/v1/auth/**" → 인증 필요
- 공개 API: "/v1/public/**" → 인증 불필요

// 사용자 ID 자동 주입
@AccountID UUID accountId  // JWT에서 자동 추출
```

### 페이징 및 검색
```java
// 표준 페이징 파라미터
@RequestParam(value = "page", defaultValue = "1") @Min(1) Integer page,
@RequestParam(value = "size", defaultValue = "10") @Min(1) Integer size,
@RequestParam(value = "sort", defaultValue = "order-date") String sort,
@RequestParam(value = "direction", defaultValue = "ASC") Direction direction

// 검색 및 필터링
@RequestParam(value = "search-type", required = false) String searchType,
@RequestParam(value = "search", required = false) String search,
@RequestParam(value = "start-date", required = false) String startDate,
@RequestParam(value = "end-date", required = false) String endDate,
@RequestParam(value = "status", defaultValue = "all") String status
```

### 파일 처리
```java
// S3 파일 업로드
MultipartFile → S3 → URL 반환

// PDF 다운로드 (Presigned URL)
시간 제한된 다운로드 URL 생성
```

### 에러 처리
```java
// 표준 응답 구조
ResponseDto.ok(data)      // 성공 응답
ResponseDto.created(data) // 생성 응답
ResponseDto.fail(e)       // 실패 응답

// 글로벌 예외 처리
@RestControllerAdvice
- CommonException 처리
- Slack 알림 자동 발송
- 일관된 에러 응답 형식

// 커스텀 예외 사용
throw new CommonException(ErrorCode.NOT_FOUND_ORDER, "주문 ID: " + orderId);
```

### 트랜잭션 처리
```java
// 서비스 레벨 트랜잭션
@Transactional  // 트랜잭션 경계 설정

// 트랜잭션 후 이벤트 처리
TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
    @Override
    public void afterCommit() {
        kakaoMessageUtil.sendCreateOrderMessage(order);
    }
});
```

### 캐싱
```java
// Redis 캐싱
@RedisHash(value = "refresh_token", timeToLive = 60 * 60 * 24 * 14)
- RefreshToken 저장 (14일 TTL)
- AuthenticationCode 저장 (시간 제한 인증코드)
- TestMailHistory 저장 (테스트 메일 발송 기록)
```

### 이벤트 처리
```java
// 이벤트 발행
eventPublisher.publishEvent(new OrderCreatedEvent(order));

// 이벤트 리스너
@Async
@EventListener
public void handleOrderCreated(OrderCreatedEvent event) {
    // 비동기 처리 (이메일 발송, 알림 등)
}
```

### 외부 서비스 연동
```java
// OAuth2 소셜 로그인 (카카오, 구글, 네이버)
// 외부 서비스 Util 클래스
@Component
public class TossPaymentUtil {
    public PaymentResult processPayment(PaymentRequest request) {
        // Toss API 호출
    }
}
```

### 유효성 검증
```java
// Request DTO 검증
@NotNull @Valid List<DocumentRequest> documents

// 커스텀 유효성 검증
@EnumValue(enumClass = EOrderStatus.class)
@ValidPassword
@DateValue
```

### HTTP 메서드 및 상태코드
- **POST**: 생성 → 201 Created
- **GET**: 조회 → 200 OK
- **PUT**: 전체 업데이트 → 200 OK
- **PATCH**: 부분 업데이트 → 200 OK
- **DELETE**: 삭제 → 200 OK

## 개발 체크리스트
1. **Controller 분류**: Admin/User, Command/Query 올바른 위치 확인
2. **UseCase 정의**: 인터페이스로 비즈니스 로직 추상화
3. **Service 구현**: Repository 주입, 트랜잭션 관리
4. **DTO 검증**: 유효성 검사 및 JSON 프로퍼티 설정
5. **Domain Service**: Repository 의존성 없이 순수 로직만 구현
6. **Repository**: 인터페이스 분리 및 QueryDSL 활용
7. **예외 처리**: 커스텀 예외 및 일관된 에러 응답
8. **인증/인가**: JWT 토큰 및 권한 확인
9. **API 문서화**: Swagger 어노테이션 추가
10. **트랜잭션**: 적절한 트랜잭션 경계 설정
11. **이벤트 처리**: 필요시 비동기 이벤트 활용
12. **외부 서비스**: Util 클래스를 통한 외부 API 호출

## 개발 규칙
- **커밋 컨벤션**: `type/#issue-number: description`
- **코드 스타일**: 기존 코드 패턴 준수
- **테스트**: 새로운 기능 추가 시 테스트 코드 작성 필수
- **API 문서**: Swagger UI (`/swagger-ui.html`)

## Claude Code 프롬프트

### Issue 생성 프롬프트
다음 정보를 기반으로 GitHub Issue를 생성하고 branch를 생성해 주세요.

1. **기본 정보**:
   - 조직 이름: TOOK-SCAN
   - 레포지토리 이름: TOOK-SCAN-Backend

2. **이슈 제목 및 내용**:
   - `.github/ISSUE_TEMPLATE/` 디렉터리 내부 파일 형식에 맞춰 작성
   - 새로운 기능 추가: `feature.md` 참고 (✨ Feature - 접두사)
   - 버그 수정: `fix.md` 참고 (🔨 Fix - 접두사)
   - 코드 리팩토링: `refactor.md` 참고 (♻️ Refactor - 접두사)
   - 문서 수정: `docs.md` 참고 (📃 Docs - 접두사)
   - 설정 수정: `setting.md` 참고 (⚙️ Setting - 접두사)
   - 테스트 관련: `test.md` 참고 (✅ Test - 접두사)
   - 배포 관련: `deploy.md` 참고 (🌏 Deploy - 접두사)
   - 크로스 브라우징: `crossBrowsing.md` 참고 (💻 CrossBrowsing - 접두사)

3. **브랜치 생성**:
   - GitHub API로 원격 저장소에 브랜치 생성 (dev 브랜치 기준)
   - 브랜치 이름: `Type/#이슈번호` 형식
   - Type은 대문자로 시작 (Feature, Fix, Refactor, Docs, Setting, Test, Deploy, CrossBrowsing)

4. **로컬 작업 환경 설정**:
   ```bash
   # 원격 저장소 최신 정보 가져오기
   git fetch origin
   
   # 원격 브랜치로 이동
   git checkout "Type/#이슈번호"
   ```

### PR 생성 프롬프트
다음 정보를 기반으로 GitHub PR을 생성해 주세요.

1. **기본 정보**:
   - 조직 이름: TOOK-SCAN
   - 레포지토리 이름: TOOK-SCAN-Backend
   - 타겟 브랜치: dev

2. **PR 제목**: 이슈 제목과 동일하게 작성

3. **PR 내용**: `.github/pull_request_template.md` 파일 형식에 맞춰 작성하여 현재 브랜치의 변경사항을 반영하여 상세하게 작성