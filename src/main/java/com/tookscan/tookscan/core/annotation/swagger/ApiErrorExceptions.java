package com.tookscan.tookscan.core.annotation.swagger;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 특정 API 엔드포인트에서 발생 가능한 일반적인 예외들을 Swagger 문서에 자동으로 추가하는 어노테이션
 * 
 * 사용 예시:
 * @ApiErrorExceptions({
 *     MethodArgumentNotValidException.class,
 *     MissingServletRequestParameterException.class
 * })
 * @PostMapping("/users")
 * public ResponseEntity<User> createUser(@Valid @RequestBody CreateUserRequest request) { ... }
 * 
 * 이 어노테이션이 적용된 API는 Swagger UI에서 해당 예외들의 상세한 응답 예시를 볼 수 있습니다.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ApiErrorExceptions {
    /**
     * 해당 API에서 발생할 수 있는 일반적인 예외 클래스들
     * 
     * @return 예외 클래스 배열
     */
    Class<? extends Exception>[] value();
}