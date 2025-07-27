package com.tookscan.tookscan.order.presentation.scheduler;

import com.tookscan.tookscan.order.application.usecase.CancelOldApplyCompletedOrdersUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class CancelOldApplyCompletedOrdersScheduler {

    private final CancelOldApplyCompletedOrdersUseCase cancelOldApplyCompletedOrdersUseCase;

    @Scheduled(cron = "0 0 0 * * ?", zone = "Asia/Seoul")
    public void cleanupOrders() {
        log.info("Canceling old apply completed orders scheduled task started");
        cancelOldApplyCompletedOrdersUseCase.execute();
    }
}
