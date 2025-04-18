package com.tookscan.tookscan.core.exception.handler;

import com.tookscan.tookscan.core.dto.SendSlackErrorDto;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ExceptionCatchHandler {

    private final ApplicationEventPublisher applicationEventPublisher;

    @PostConstruct
    public void registerUncaughtExceptionHandler() {
        Thread.setDefaultUncaughtExceptionHandler((thread, throwable) -> {
            applicationEventPublisher.publishEvent(
                    SendSlackErrorDto.of(
                            throwable
                    )
            );
        });
    }
}
