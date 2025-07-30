# BusinessLog AOP 로깅 시스템 사용 가이드

## 개요
`@BusinessLog` 어노테이션을 사용하여 비즈니스 로직의 시작/종료 로깅을 자동화하는 AOP 시스템입니다.
기존의 반복적인 로깅 코드를 제거하고, 일관된 구조화된 로깅을 제공합니다.

### 관심사 분리 (Separation of Concerns)
이 AOP 로깅 시스템은 **정상적인 비즈니스 로직 흐름만** 담당합니다:
- ✅ **AOP 로깅**: 비즈니스 로직 시작/완료 로깅
- ✅ **ExceptionHandler**: 예외 발생 시 로깅 및 에러 응답 처리

이러한 설계로 각 컴포넌트의 책임이 명확히 분리되어 유지보수성이 향상됩니다.

## 기본 사용법

### 1. 간단한 사용 예시
```java
@Service
public class CreateUserService implements CreateUserUseCase {
    
    @Override
    @Transactional
    @BusinessLog(
        domain = "Account",
        action = "create user"
    )
    public User execute(CreateUserRequestDto requestDto) {
        // 비즈니스 로직
        return createdUser;
    }
}
```

**로그 출력 결과:**
```
[Account] Admin create user process started
[Account] Admin create user completed successfully {"execution_time_ms": 45}
```

### 2. 상세 정보를 포함한 사용 예시
```java
@BusinessLog(
    domain = "Account",
    action = "create group",
    userType = "Admin",
    startDetails = {"group_name: #requestDto.name()"},
    endDetails = {"group_id: #group.getId()", "group_name: #group.getName()"}
)
public Group execute(CreateAdminGroupRequestDto requestDto) {
    // 비즈니스 로직
    return group;
}
```

**로그 출력 결과:**
```
[Account] Admin create group process started {"details": {"group_name": "관리자그룹"}}
[Account] Admin create group completed successfully {"details": {"group_id": 12345, "group_name": "관리자그룹"}, "execution_time_ms": 23}
```

## 어노테이션 속성 설명

### 필수 속성
- **`domain`**: 도메인 이름 (예: "Account", "Order", "Payment")
- **`action`**: 비즈니스 액션 설명 (예: "create user", "update order", "delete group")

### 선택적 속성
- **`userType`**: 사용자 구분 (기본값: "Admin")
- **`startMessage`**: 시작 로그 메시지 템플릿 (SpEL 지원)
- **`endMessage`**: 종료 로그 메시지 템플릿 (SpEL 지원)
- **`startDetails`**: 시작 로그의 details 필드 배열
- **`endDetails`**: 종료 로그의 details 필드 배열
- **`includeExecutionTime`**: 실행 시간 포함 여부 (기본값: true)
- **`level`**: 로그 레벨 (기본값: INFO)

## SpEL(Spring Expression Language) 사용법

### 1. 기본 변수 접근
```java
@BusinessLog(
    startDetails = {
        "user_id: #userId",           // 메서드 파라미터
        "email: #requestDto.email()", // 객체 메서드 호출
        "domain: #domain",            // 어노테이션 속성값
        "action: #action"             // 어노테이션 속성값
    }
)
```

### 2. 결과값 접근 (endDetails에서 사용)
```java
@BusinessLog(
    endDetails = {
        "user_id: #result.getId()",     // #result로 접근
        "user_name: #user.getName()",   // 타입명으로 접근 (User -> #user)
        "order_id: #order.getId()"      // 타입명으로 접근 (Order -> #order)
    }
)
```

### 3. 복잡한 표현식
```java
@BusinessLog(
    startDetails = {
        "order_count: #orderList.size()",
        "total_amount: #requestDto.orders.![amount].sum()",
        "has_discount: #requestDto.discountCode != null"
    }
)
```

## 다양한 사용 시나리오

### 1. 생성(Create) 작업
```java
@BusinessLog(
    domain = "Order",
    action = "create order",
    userType = "User",
    startDetails = {"customer_id: #customerId", "item_count: #requestDto.items.size()"},
    endDetails = {"order_id: #order.getId()", "total_amount: #order.getTotalAmount()"}
)
public Order execute(CreateOrderRequestDto requestDto, UUID customerId) {
    // 주문 생성 로직
    return order;
}
```

### 2. 수정(Update) 작업
```java
@BusinessLog(
    domain = "Account",
    action = "update user profile",
    startDetails = {"user_id: #userId", "fields: #requestDto.getChangedFields()"},
    endDetails = {"user_id: #user.getId()", "updated_at: #user.getUpdatedAt()"}
)
public User execute(UpdateUserRequestDto requestDto, UUID userId) {
    // 사용자 정보 수정 로직
    return user;
}
```

### 3. 삭제(Delete) 작업
```java
@BusinessLog(
    domain = "Order",
    action = "cancel orders",
    startDetails = {"order_ids: #requestDto.orderIds()", "reason: #requestDto.reason()"},
    endDetails = {"cancelled_count: #requestDto.orderIds().size()"}
)
public void execute(CancelOrdersRequestDto requestDto) {
    // 주문 취소 로직
}
```

### 4. 조회(Read) 작업 (필요시)
```java
@BusinessLog(
    domain = "Account",
    action = "search users",
    level = BusinessLog.LogLevel.DEBUG,
    startDetails = {"search_keyword: #searchKeyword", "page: #page"},
    endDetails = {"result_count: #result.size()"}
)
public List<User> execute(String searchKeyword, int page) {
    // 사용자 검색 로직
    return users;
}
```

## 성능 최적화 기능

1. **SpEL 표현식 캐싱**: 같은 표현식은 한 번만 파싱하고 캐시에 저장
2. **Logger 인스턴스 캐싱**: 클래스별 Logger 인스턴스 재사용
3. **조건부 로깅**: 로그 레벨에 따른 조건부 실행

## 마이그레이션 가이드

### Before (기존 코드)
```java
@Service
public class CreateUserService {
    private static final Logger log = LoggerFactory.getLogger(CreateUserService.class);
    
    public void execute(CreateUserRequestDto requestDto) {
        // StructuredLoggerUtil.info(log)
        //     .message("[Account] Admin create user process started")
        //     .details(Map.of("email", requestDto.email()))
        //     .log();
            
        // 비즈니스 로직
        User user = createUser(requestDto);
        
        // StructuredLoggerUtil.info(log)
        //     .message("[Account] Admin user created successfully")
        //     .details(Map.of("user_id", user.getId(), "email", user.getEmail()))
        //     .log();
    }
}
```

### After (AOP 적용)
```java
@Service
public class CreateUserService {
    
    @BusinessLog(
        domain = "Account",
        action = "create user",
        startDetails = {"email: #requestDto.email()"},
        endDetails = {"user_id: #user.getId()", "email: #user.getEmail()"}
    )
    public User execute(CreateUserRequestDto requestDto) {
        // 비즈니스 로직만 집중
        User user = createUser(requestDto);
        return user; // 결과 반환으로 endDetails에서 사용 가능
    }
}
```

## 주의사항

1. **반환값**: endDetails에서 결과값을 사용하려면 메서드가 값을 반환해야 합니다.
2. **SpEL 표현식**: 잘못된 표현식은 예외를 발생시키지 않고 원본 문자열을 출력합니다.
3. **성능**: 로그 레벨이 DEBUG인 경우 운영 환경에서는 출력되지 않을 수 있습니다.
4. **트랜잭션**: AOP는 프록시 기반이므로 같은 클래스 내부 메서드 호출에서는 작동하지 않습니다.

## 설정 확인

1. **build.gradle**에 AOP 의존성 확인:
```gradle
implementation 'org.springframework.boot:spring-boot-starter-aop'
```

2. **AopConfig** 클래스로 AspectJ 활성화 확인:
```java
@Configuration
@EnableAspectJAutoProxy
public class AopConfig {
}
```

3. **BusinessLogAspect**가 Spring Bean으로 등록되어 있는지 확인