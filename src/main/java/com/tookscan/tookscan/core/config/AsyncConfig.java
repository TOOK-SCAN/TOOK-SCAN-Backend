package com.tookscan.tookscan.core.config;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@EnableAsync
@Slf4j
public class AsyncConfig implements AsyncConfigurer {

    private static final int PROCESSORS = Runtime.getRuntime().availableProcessors();

    /**
     * 이메일 발송용 전용 스레드 풀
     * - 중간 우선순위, 안정성 중시
     * - 이메일 발송 작업 전담
     */
    @Bean(name = "emailTaskExecutor")
    public ThreadPoolTaskExecutor emailTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(PROCESSORS);
        executor.setMaxPoolSize(PROCESSORS * 2);
        executor.setQueueCapacity(100);
        executor.setKeepAliveSeconds(60);
        executor.setThreadNamePrefix("email-async-");
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(30);
        executor.initialize();
        log.atInfo()
            .addKeyValue("corePoolSize", executor.getCorePoolSize())
            .addKeyValue("maxPoolSize", executor.getMaxPoolSize())
            .addKeyValue("queueCapacity", executor.getQueueCapacity())
            .log("[Async Thread] Email TaskExecutor initialized");

        return executor;
    }

    /**
     * 알림 발송용 전용 스레드 풀 (SMS, Slack)
     * - 높은 처리량 필요
     * - 빠른 응답 시간 요구
     */
    @Bean(name = "notificationTaskExecutor")
    public ThreadPoolTaskExecutor notificationTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(PROCESSORS);
        executor.setMaxPoolSize(PROCESSORS * 2);
        executor.setQueueCapacity(200);
        executor.setKeepAliveSeconds(60);
        executor.setThreadNamePrefix("notification-async-");
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(30);
        executor.initialize();

        log.atInfo()
            .addKeyValue("corePoolSize", executor.getCorePoolSize())
            .addKeyValue("maxPoolSize", executor.getMaxPoolSize())
            .addKeyValue("queueCapacity", executor.getQueueCapacity())
            .log("[Async Thread] Notification TaskExecutor initialized");
        
        return executor;
    }

    /**
     * 파일 처리용 전용 스레드 풀
     * - CPU 집약적 작업
     * - 제한된 동시 실행
     */
    @Bean(name = "fileProcessingTaskExecutor")
    public ThreadPoolTaskExecutor fileProcessingTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(1);
        executor.setMaxPoolSize(PROCESSORS);
        executor.setQueueCapacity(50);
        executor.setKeepAliveSeconds(120);
        executor.setThreadNamePrefix("file-processing-async-");
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(60);
        executor.initialize();

        log.atInfo()
            .addKeyValue("corePoolSize", executor.getCorePoolSize())
            .addKeyValue("maxPoolSize", executor.getMaxPoolSize())
            .addKeyValue("queueCapacity", executor.getQueueCapacity())
            .log("[Async Thread] File Processing TaskExecutor initialized");

        return executor;
    }

    /**
     * 스케줄러 전용 스레드 풀 - 스케줄러 작업 전담 - 장시간 실행 작업 고려
     */
    @Bean(name = "schedulerTaskExecutor")
    public ThreadPoolTaskExecutor schedulerTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(PROCESSORS);
        executor.setMaxPoolSize(PROCESSORS * 2);
        executor.setQueueCapacity(10);
        executor.setKeepAliveSeconds(300);
        executor.setThreadNamePrefix("scheduler-async-");
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(120);
        executor.initialize();

        log.atInfo()
            .addKeyValue("corePoolSize", executor.getCorePoolSize())
            .addKeyValue("maxPoolSize", executor.getMaxPoolSize())
            .addKeyValue("queueCapacity", executor.getQueueCapacity())
            .log("[Async Thread] Scheduler TaskExecutor initialized");

        return executor;
    }

    /**
     * 기본 스레드 풀 설정
     * - 명시되지 않은 @Async 작업용
     */
    @Override
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(PROCESSORS);
        executor.setMaxPoolSize(PROCESSORS + 1);
        executor.setQueueCapacity(150);
        executor.setKeepAliveSeconds(60);
        executor.setThreadNamePrefix("default-async-");
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(30);
        executor.initialize();

        log.atInfo()
            .addKeyValue("corePoolSize", executor.getCorePoolSize())
            .addKeyValue("maxPoolSize", executor.getMaxPoolSize())
            .addKeyValue("queueCapacity", executor.getQueueCapacity())
            .log("[Async Thread] Default TaskExecutor initialized");

        return executor;
    }

    /**
     * 비동기 작업 예외 처리 핸들러
     */
    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return new CustomAsyncExceptionHandler();
    }

    /**
     * 커스텀 비동기 예외 핸들러
     */
    private static class CustomAsyncExceptionHandler implements AsyncUncaughtExceptionHandler {
        
        @Override
        public void handleUncaughtException(Throwable ex, Method method, Object... params) {
            
            // 파라미터 정보 로깅
            if (params != null && params.length > 0) {
                log.atError()
                    .setCause(ex)
                    .addKeyValue("method", method.getName())
                    .addKeyValue("params", params)
                    .log("[Async Error] Uncaught exception in async method");
            }
        }
    }
}