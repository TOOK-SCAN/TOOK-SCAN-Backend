package com.tookscan.tookscan.core.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * AOP(Aspect-Oriented Programming) 설정
 * 
 * @BusinessLog 어노테이션을 통한 비즈니스 로직 로깅 자동화를 위해
 * AspectJ Auto Proxy를 활성화합니다.
 */
@Configuration
@EnableAspectJAutoProxy
public class AopConfig {
    
    // AspectJ Auto Proxy 활성화만으로 충분함
    // BusinessLogAspect는 @Component로 자동 등록됨
}