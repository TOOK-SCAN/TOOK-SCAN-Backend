package com.tookscan.tookscan.core.util;

import java.util.HashMap;
import java.util.Map;

/**
 * 간단한 비즈니스 로그 컨텍스트 유틸리티
 * 
 * 사용 예시:
 * <pre>
 * LogContext.put("old_status", oldStatus);
 * LogContext.put("group_id", groupId);
 * LogContext.put("customer_type", "VIP");
 * </pre>
 */
public class LogContext {
    
    private static final ThreadLocal<Map<String, Object>> context = new ThreadLocal<>();
    
    /**
     * 간단한 키-값 쌍 추가
     */
    public static void put(String key, Object value) {
        if (key != null && value != null) {
            getContext().put(key, value);
        }
    }
    
    /**
     * 조건부로 키-값 쌍 추가
     */
    public static void putIf(boolean condition, String key, Object value) {
        if (condition && key != null && value != null) {
            getContext().put(key, value);
        }
    }
    
    /**
     * 여러 값을 한번에 추가
     */
    public static void putAll(Map<String, Object> values) {
        if (values != null && !values.isEmpty()) {
            getContext().putAll(values);
        }
    }
    
    /**
     * 특정 키의 값 조회
     */
    public static Object get(String key) {
        Map<String, Object> ctx = context.get();
        return ctx != null ? ctx.get(key) : null;
    }
    
    /**
     * 모든 컨텍스트 값 조회
     */
    public static Map<String, Object> getAll() {
        Map<String, Object> ctx = context.get();
        return ctx != null ? new HashMap<>(ctx) : new HashMap<>();
    }
    
    /**
     * 컨텍스트가 비어있는지 확인
     */
    public static boolean isEmpty() {
        Map<String, Object> ctx = context.get();
        return ctx == null || ctx.isEmpty();
    }
    
    /**
     * 컨텍스트 정리 (내부적으로 사용, 일반적으로 호출하지 않음)
     */
    public static void clear() {
        context.remove();
    }
    
    private static Map<String, Object> getContext() {
        Map<String, Object> ctx = context.get();
        if (ctx == null) {
            ctx = new HashMap<>();
            context.set(ctx);
        }
        return ctx;
    }
}