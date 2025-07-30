package com.tookscan.tookscan.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 비즈니스 로직 실행 로깅을 위한 AOP 어노테이션
 * 메서드 시작/종료 시점의 구조화된 로깅을 자동화합니다.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface BusinessLog {
    
    /**
     * 도메인 이름 (로그 메시지 prefix로 사용)
     * 예: "Account", "Order", "Payment"
     */
    String domain();
    
    /**
     * 비즈니스 액션 설명
     * 예: "create group", "update user", "delete order"
     */
    String action();
    
    /**
     * 사용자 구분 (Admin/User)
     * 기본값: "Admin"
     */
    String userType() default "Admin";
    
    /**
     * 시작 로그 메시지 템플릿 (SpEL 지원)
     * 기본값: "[{domain}] {userType} {action} process started"
     * 
     * SpEL 변수 사용 가능:
     * - #domain: 도메인 이름
     * - #action: 액션 이름
     * - #userType: 사용자 타입
     * - #args: 메서드 파라미터 배열
     * - #paramName: 파라미터명으로 직접 접근
     */
    String startMessage() default "[#{#domain}] #{#userType} #{#action} process started";
    
    /**
     * 종료 로그 메시지 템플릿 (SpEL 지원)
     * 기본값: "[{domain}] {userType} {action} completed successfully"
     */
    String endMessage() default "[#{#domain}] #{#userType} #{#action} completed successfully";
    
    /**
     * 시작 로그의 details에 포함할 파라미터 필드들 (SpEL 지원)
     * 
     * 예시:
     * - {"group_name": "#requestDto.name()"}
     * - {"user_id": "#userId", "email": "#requestDto.email()"}
     * - {"order_ids": "#requestDto.orderIds()"}
     */
    String[] startDetails() default {};
    
    /**
     * 종료 로그의 details에 포함할 결과 필드들 (SpEL 지원)
     * 
     * 예시:
     * - {"group_id": "#result.getId()", "group_name": "#result.getName()"}
     * - {"updated_user_count": "#result"}
     * - {"created_order_id": "#result.orderId"}
     */
    String[] endDetails() default {};
    
    /**
     * 실행 시간 로깅 여부
     * 기본값: true
     */
    boolean includeExecutionTime() default true;
    
    /**
     * 로그 레벨
     * 기본값: INFO
     */
    LogLevel level() default LogLevel.INFO;
    
    enum LogLevel {
        DEBUG, INFO, WARN, ERROR
    }
}