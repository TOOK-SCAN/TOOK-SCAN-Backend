package com.tookscan.tookscan.core.exception.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // Method Not Allowed Error
    METHOD_NOT_ALLOWED(40500, HttpStatus.METHOD_NOT_ALLOWED, "지원하지 않는 HTTP 메소드입니다."),

    // Not Found Error
    NOT_FOUND_END_POINT(40400, HttpStatus.NOT_FOUND, "존재하지 않는 API 엔드포인트입니다."),
    NOT_FOUND_AUTHORIZATION_HEADER(40401, HttpStatus.NOT_FOUND, "Authorization 헤더가 존재하지 않습니다."),
    NOT_FOUND_ACCOUNT(40402, HttpStatus.NOT_FOUND, "존재하지 않는 사용자입니다."),
    NOT_FOUND_TEMPORARY_ACCOUNT(40403, HttpStatus.NOT_FOUND, "존재하지 않는 임시 사용자입니다."),
    NOT_FOUND_RESOURCE(40404, HttpStatus.NOT_FOUND, "해당 리소스가 존재하지 않습니다."),
    NOT_FOUND_TYPE(40404, HttpStatus.NOT_FOUND ,"타입이 존재하지 않습니다." ),
    NOT_FOUND_ORDER(40405, HttpStatus.NOT_FOUND, "존재하지 않는 주문입니다."),
    NOT_FOUND_DOCUMENT(40406, HttpStatus.NOT_FOUND, "존재하지 않는 문서입니다."),
    NOT_FOUND_PRICE_POLICY(40407, HttpStatus.NOT_FOUND, "존재하지 않는 가격 정책입니다."),
    NOT_FOUND_DELIVERY(40408, HttpStatus.NOT_FOUND, "존재하지 않는 배송입니다."),
    NOT_FOUND_PAYMENT(40409, HttpStatus.NOT_FOUND, "존재하지 않는 결제입니다."),
    NOT_FOUND_TERM(40410, HttpStatus.NOT_FOUND, "존재하지 않는 약관입니다."),
    NOT_FOUND_USER_GROUP(40411, HttpStatus.NOT_FOUND, "존재하지 않는 사용자 그룹입니다."),
    NOT_FOUND_AUTHENTICATION_CODE(40413, HttpStatus.NOT_FOUND, "존재하지 않는 인증 코드입니다."),
    NOT_FOUND_PAYMENT_SESSION(40414, HttpStatus.NOT_FOUND, "결제 시간이 만료되어 결제 진행 데이터가 존재하지 않습니다."),
    NOT_FOUND_COUPON(40415, HttpStatus.NOT_FOUND, "존재하지 않는 쿠폰입니다."),
    NOT_FOUND_PDF_FILE(40416, HttpStatus.NOT_FOUND, "존재하지 않는 PDF 파일입니다."),


    // Invalid Argument Error
    MISSING_REQUEST_PARAMETER(40000, HttpStatus.BAD_REQUEST, "필수 요청 파라미터가 누락되었습니다."),
    INVALID_ARGUMENT(40001, HttpStatus.BAD_REQUEST, "요청에 유효하지 않은 인자입니다."),
    INVALID_PARAMETER_FORMAT(40002, HttpStatus.BAD_REQUEST, "요청에 유효하지 않은 인자 형식입니다."),
    INVALID_HEADER_ERROR(40003, HttpStatus.BAD_REQUEST, "유효하지 않은 헤더입니다."),
    MISSING_REQUEST_HEADER(40004, HttpStatus.BAD_REQUEST, "필수 요청 헤더가 누락되었습니다."),
    BAD_REQUEST_PARAMETER(40005, HttpStatus.BAD_REQUEST, "잘못된 요청 파라미터입니다."),
    UNSUPPORTED_MEDIA_TYPE(40006, HttpStatus.BAD_REQUEST, "지원하지 않는 미디어 타입입니다."),
    BAD_REQUEST_JSON(40007, HttpStatus.BAD_REQUEST, "잘못된 JSON 형식입니다."),
    INVALID_ACCOUNT_TYPE(40008, HttpStatus.BAD_REQUEST, "Account Type이 잘못되었습니다."),
    INVALID_PRINCIPAL_TYPE(40009, HttpStatus.BAD_REQUEST, "Principal Type이 잘못되었습니다."),
    ALREADY_EXIST_RESOURCE(40010, HttpStatus.BAD_REQUEST, "이미 존재하는 리소스입니다."),
    INVALID_ENUM_TYPE(40011, HttpStatus.BAD_REQUEST, "유효하지 않은 Enum 타입입니다."),
    PAYMENT_INCOMPLETE(40012, HttpStatus.BAD_REQUEST, "결제가 완료되지 않았습니다."),
    INVALID_ORDER_STATUS(40013, HttpStatus.BAD_REQUEST, "유효하지 않은 주문 상태입니다."),
    ALREADY_PROCESSED_PAYMENT(40014, HttpStatus.BAD_REQUEST, "이미 처리된 결제 입니다."),
    PROVIDER_ERROR(40015, HttpStatus.BAD_REQUEST, "일시적인 오류가 발생했습니다. 잠시 후 다시 시도해주세요."),
    EXCEED_MAX_CARD_INSTALLMENT_PLAN(40016, HttpStatus.BAD_REQUEST, "설정 가능한 최대 할부 개월 수를 초과했습니다."),
    NOT_ALLOWED_POINT_USE(40017, HttpStatus.BAD_REQUEST, "포인트 사용이 불가한 카드로 카드 포인트 결제에 실패했습니다."),
    INVALID_API_KEY(40018, HttpStatus.BAD_REQUEST, "잘못된 시크릿키 연동 정보 입니다."),
    INVALID_REJECT_CARD(40019, HttpStatus.BAD_REQUEST, "카드 사용이 거절되었습니다. 카드사 문의가 필요합니다."),
    BELOW_MINIMUM_AMOUNT(40020, HttpStatus.BAD_REQUEST, "신용카드는 결제금액이 100원 이상, 계좌는 200원이상부터 결제가 가능합니다."),
    INVALID_CARD_EXPIRATION(40021, HttpStatus.BAD_REQUEST, "카드 정보를 다시 확인해주세요. (유효기간)"),
    INVALID_STOPPED_CARD(40022, HttpStatus.BAD_REQUEST, "정지된 카드 입니다."),
    EXCEED_MAX_DAILY_PAYMENT_COUNT(40023, HttpStatus.BAD_REQUEST, "하루 결제 가능 횟수를 초과했습니다."),
    NOT_SUPPORTED_INSTALLMENT_PLAN_CARD_OR_MERCHANT(40024, HttpStatus.BAD_REQUEST, "할부가 지원되지 않는 카드 또는 가맹점 입니다."),
    INVALID_CARD_INSTALLMENT_PLAN(40025, HttpStatus.BAD_REQUEST, "할부 개월 정보가 잘못되었습니다."),
    NOT_SUPPORTED_MONTHLY_INSTALLMENT_PLAN(40026, HttpStatus.BAD_REQUEST, "할부가 지원되지 않는 카드입니다."),
    EXCEED_MAX_PAYMENT_AMOUNT(40027, HttpStatus.BAD_REQUEST, "하루 결제 가능 금액을 초과했습니다."),
    NOT_FOUND_TERMINAL_ID(40028, HttpStatus.BAD_REQUEST, "단말기번호(Terminal Id)가 없습니다. 토스페이먼츠로 문의 바랍니다."),
    INVALID_AUTHORIZE_AUTH(40029, HttpStatus.BAD_REQUEST, "유효하지 않은 인증 방식입니다."),
    INVALID_CARD_LOST_OR_STOLEN(40030, HttpStatus.BAD_REQUEST, "분실 혹은 도난 카드입니다."),
    RESTRICTED_TRANSFER_ACCOUNT(40031, HttpStatus.BAD_REQUEST, "계좌는 등록 후 12시간 뒤부터 결제할 수 있습니다. 관련 정책은 해당 은행으로 문의해주세요."),
    INVALID_CARD_NUMBER(40032, HttpStatus.BAD_REQUEST, "카드번호를 다시 확인해주세요."),
    INVALID_UNREGISTERED_SUBMALL(40033, HttpStatus.BAD_REQUEST, "등록되지 않은 서브몰입니다. 서브몰이 없는 가맹점이라면 안심클릭이나 ISP 결제가 필요합니다."),
    NOT_REGISTERED_BUSINESS(40034, HttpStatus.BAD_REQUEST, "등록되지 않은 사업자 번호입니다."),
    EXCEED_MAX_ONE_DAY_WITHDRAW_AMOUNT(40035, HttpStatus.BAD_REQUEST, "1일 출금 한도를 초과했습니다."),
    EXCEED_MAX_ONE_TIME_WITHDRAW_AMOUNT(40036, HttpStatus.BAD_REQUEST, "1회 출금 한도를 초과했습니다."),
    CARD_PROCESSING_ERROR(40037, HttpStatus.BAD_REQUEST, "카드사에서 오류가 발생했습니다."),
    EXCEED_MAX_AMOUNT(40038, HttpStatus.BAD_REQUEST, "거래금액 한도를 초과했습니다."),
    INVALID_ACCOUNT_INFO_RE_REGISTER(40039, HttpStatus.BAD_REQUEST, "유효하지 않은 계좌입니다. 계좌 재등록 후 시도해주세요."),
    NOT_AVAILABLE_PAYMENT(40040, HttpStatus.BAD_REQUEST, "결제가 불가능한 시간대입니다."),
    UNAPPROVED_ORDER_ID(40041, HttpStatus.BAD_REQUEST, "아직 승인되지 않은 주문번호입니다."),
    EXCEED_MAX_MONTHLY_PAYMENT_AMOUNT(40042, HttpStatus.BAD_REQUEST, "당월 결제 가능금액인 1,000,000원을 초과 하셨습니다."),
    SORT_ORDER_OUT_OF_RANGE(40043, HttpStatus.BAD_REQUEST, "정렬 순서가 범위를 벗어났습니다."),
    SORT_ORDER_DUPLICATE(40044, HttpStatus.BAD_REQUEST, "정렬 순서가 중복되었습니다."),
    SORT_ORDER_NOT_CONTINUOUS(40045, HttpStatus.BAD_REQUEST, "정렬 순서가 연속되지 않았습니다."),
    NOT_AVAILABLE_COUPON(40046, HttpStatus.BAD_REQUEST, "사용할 수 없는 쿠폰입니다."),
    USED_COUPON(40047, HttpStatus.BAD_REQUEST, "이미 사용된 쿠폰입니다."),
    USER_ONLY_COUPON(40048, HttpStatus.BAD_REQUEST, "회원 전용 쿠폰입니다."),

    // SIGN UP Error
    ALREADY_EXIST_ID(40200, HttpStatus.BAD_REQUEST, "이미 존재하는 아이디입니다."),
    ALREADY_EXIST_PHONE_NUMBER(40201, HttpStatus.BAD_REQUEST, "이미 존재하는 휴대폰 번호입니다."),

    // Access Denied Error
    ACCESS_DENIED(40300, HttpStatus.FORBIDDEN, "접근 권한이 없습니다."),
    NOT_LOGIN_USER(40301, HttpStatus.FORBIDDEN, "로그인하지 않은 사용자입니다."),
    NOT_MATCH_AUTHENTICATION_CODE(40302, HttpStatus.FORBIDDEN, "인증 코드가 일치하지 않습니다."),
    NOT_MATCH_ORDER_USER(40303, HttpStatus.FORBIDDEN, "주문자가 일치하지 않습니다."),
    REJECT_ACCOUNT_PAYMENT(40304, HttpStatus.FORBIDDEN, "잔액부족으로 결제에 실패했습니다."),
    REJECT_CARD_PAYMENT(40305, HttpStatus.FORBIDDEN, "한도초과 혹은 잔액부족으로 결제에 실패했습니다."),
    REJECT_CARD_COMPANY(40306, HttpStatus.FORBIDDEN, "결제 승인이 거절되었습니다."),
    FORBIDDEN_REQUEST(40307, HttpStatus.FORBIDDEN, "허용되지 않은 요청입니다."),
    REJECT_TOSSPAY_INVALID_ACCOUNT(40308, HttpStatus.FORBIDDEN, "선택하신 출금 계좌가 출금이체 등록이 되어 있지 않아요. 계좌를 다시 등록해 주세요."),
    EXCEED_MAX_AUTH_COUNT(40309, HttpStatus.FORBIDDEN, "최대 인증 횟수를 초과했습니다. 카드사로 문의해주세요."),
    EXCEED_MAX_ONE_DAY_AMOUNT(40310, HttpStatus.FORBIDDEN, "일일 한도를 초과했습니다."),
    NOT_AVAILABLE_BANK(40311, HttpStatus.FORBIDDEN, "은행 서비스 시간이 아닙니다."),
    INVALID_PASSWORD(40312, HttpStatus.FORBIDDEN, "결제 비밀번호가 일치하지 않습니다."),
    INCORRECT_BASIC_AUTH_FORMAT(40313, HttpStatus.FORBIDDEN, "잘못된 요청입니다. ':' 를 포함해 인코딩해주세요."),
    FDS_ERROR(40314, HttpStatus.FORBIDDEN, "[토스페이먼츠] 위험거래가 감지되어 결제가 제한됩니다. 발송된 문자에 포함된 링크를 통해 본인인증 후 결제가 가능합니다. (고객센터: 1644-8051)"),

    // Unauthorized Error
    FAILURE_LOGIN(40100, HttpStatus.UNAUTHORIZED, "잘못된 아이디 또는 비밀번호입니다."),
    EXPIRED_TOKEN_ERROR(40101, HttpStatus.UNAUTHORIZED, "만료된 토큰입니다."),
    INVALID_TOKEN_ERROR(40102, HttpStatus.UNAUTHORIZED, "유효하지 않은 토큰입니다."),
    TOKEN_MALFORMED_ERROR(40103, HttpStatus.UNAUTHORIZED, "토큰이 올바르지 않습니다."),
    TOKEN_TYPE_ERROR(40104, HttpStatus.UNAUTHORIZED, "토큰 타입이 일치하지 않거나 비어있습니다."),
    TOKEN_UNSUPPORTED_ERROR(40105, HttpStatus.UNAUTHORIZED, "지원하지 않는 토큰입니다."),
    TOKEN_GENERATION_ERROR(40106, HttpStatus.UNAUTHORIZED, "토큰 생성에 실패하였습니다."),
    TOKEN_UNKNOWN_ERROR(40107, HttpStatus.UNAUTHORIZED, "알 수 없는 토큰입니다."),
    NOT_VERIFIED_AUTHENTICATION_CODE(40108, HttpStatus.UNAUTHORIZED, "인증 코드 인증이 완료되지 않았습니다."),
    UNAUTHORIZED_KEY(40109, HttpStatus.UNAUTHORIZED, "인증되지 않은 시크릿 키 혹은 클라이언트 키 입니다."),
    TYPE_COEXISTENCE_ERROR(40110, HttpStatus.UNAUTHORIZED, "약관 타입은 동시에 존재할 수 없습니다."),

    // Too Many Requests Error
    TOO_FAST_AUTHENTICATION_CODE_REQUESTS(42900, HttpStatus.TOO_MANY_REQUESTS, "인증코드 발급 속도가 너무 빠릅니다."),
    TOO_MANY_AUTHENTICATION_CODE_REQUESTS(42901, HttpStatus.TOO_MANY_REQUESTS, "인증코드 발급 요청이 너무 많습니다."),

    // Internal Server Error
    INTERNAL_SERVER_ERROR(50000, HttpStatus.INTERNAL_SERVER_ERROR, "서버 내부 에러입니다."),
    INTERNAL_DATA_ERROR(50001, HttpStatus.INTERNAL_SERVER_ERROR, "서버 내부 데이터 에러입니다."),
    UPLOAD_FILE_ERROR(50002, HttpStatus.INTERNAL_SERVER_ERROR, "파일 업로드에 실패하였습니다."),
    FAILED_PAYMENT_INTERNAL_SYSTEM_PROCESSING(50003, HttpStatus.INTERNAL_SERVER_ERROR, "결제가 완료되지 않았어요. 다시 시도해주세요."),
    FAILED_INTERNAL_SYSTEM_PROCESSING(50004, HttpStatus.INTERNAL_SERVER_ERROR, "내부 시스템 처리 작업이 실패했습니다. 잠시 후 다시 시도해주세요."),
    UNKNOWN_PAYMENT_ERROR(50005, HttpStatus.INTERNAL_SERVER_ERROR, "결제에 실패했어요. 같은 문제가 반복된다면 은행이나 카드사로 문의해주세요."),

    // RestClient Error
    REST_CLIENT_ERROR(50006, HttpStatus.INTERNAL_SERVER_ERROR, "RestClient 에러입니다."),

    // External Server Error
    EXTERNAL_SERVER_ERROR(50200, HttpStatus.BAD_GATEWAY, "서버 외부 에러입니다."),
    ;


    private final Integer code;
    private final HttpStatus httpStatus;
    private final String message;
}
