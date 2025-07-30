package com.tookscan.tookscan.core.exception.handler;

import com.tookscan.tookscan.core.dto.SendSlackErrorDto;
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

            log.atError()
                .setCause(throwable)
                .addKeyValue("thread_name", thread.getName())
                .log("[Global] Uncaught exception occurred in thread");

            applicationEventPublisher.publishEvent(
                    SendSlackErrorDto.of(
                            throwable
                    )
            );
        });
    }
}
