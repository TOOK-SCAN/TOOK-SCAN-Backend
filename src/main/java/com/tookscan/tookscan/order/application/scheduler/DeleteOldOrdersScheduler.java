package com.tookscan.tookscan.order.application.scheduler;

import com.tookscan.tookscan.order.application.usecase.DeleteOldOrdersUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DeleteOldOrdersScheduler {

    private final DeleteOldOrdersUseCase deleteOldOrdersUseCase;

    @Scheduled(cron = "0 0 0 * * ?", zone = "Asia/Seoul")
    public void cleanupOrders() {
        log.info("Deleting old orders scheduled task started");
        deleteOldOrdersUseCase.execute();
    }
}
