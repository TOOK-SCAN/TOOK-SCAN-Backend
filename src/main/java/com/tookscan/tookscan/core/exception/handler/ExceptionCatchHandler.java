package com.tookscan.tookscan.core.exception.handler;

import com.tookscan.tookscan.core.dto.SendSlackErrorDto;
import com.tookscan.tookscan.core.utility.StructuredLoggerUtil;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ExceptionCatchHandler {

    private final ApplicationEventPublisher applicationEventPublisher;

    @PostConstruct
    public void registerUncaughtExceptionHandler() {
        Thread.setDefaultUncaughtExceptionHandler((thread, throwable) -> {

            StructuredLoggerUtil.error(log)
                    .message("[Global] Uncaught exception occurred in thread")
                    .field("thread_name", thread.getName())
                    .exception(throwable)
                    .log();

            applicationEventPublisher.publishEvent(
                    SendSlackErrorDto.of(
                            throwable
                    )
            );
        });
    }
}
