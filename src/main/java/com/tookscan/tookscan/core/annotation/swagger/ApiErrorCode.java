package com.tookscan.tookscan.core.annotation.swagger;

import com.tookscan.tookscan.core.exception.error.ErrorCode;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 특정 API 엔드포인트에서 발생 가능한 에러 코드를 Swagger 문서에 자동으로 추가하는 어노테이션
 * 
 * 사용 예시:
 * @ApiErrorCode(UserErrorCode.class)
 * @GetMapping("/users/{id}")
 * public ResponseEntity<User> getUser(@PathVariable Long id) { ... }
 * 
 * 이 어노테이션이 적용된 API는 Swagger UI에서 해당 에러 코드들의 상세한 응답 예시를 볼 수 있습니다.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ApiErrorCode {
    /**
     * 해당 API에서 발생할 수 있는 에러 코드들이 정의된 ErrorCode 배열
     * 
     * @return 에러 코드 배열
     */
    ErrorCode[] value();
}