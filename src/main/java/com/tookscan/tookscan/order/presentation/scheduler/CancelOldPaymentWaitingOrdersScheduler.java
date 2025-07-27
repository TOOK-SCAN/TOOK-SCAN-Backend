package com.tookscan.tookscan.order.presentation.scheduler;

import com.tookscan.tookscan.order.application.usecase.CancelOldPaymentWaitingOrdersUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class CancelOldPaymentWaitingOrdersScheduler {

    private final CancelOldPaymentWaitingOrdersUseCase cancelOldPaymentWaitingOrdersUseCase;

    @Scheduled(cron = "0 0 0 * * ?", zone = "Asia/Seoul")
    public void cleanupOrders() {
        log.info("Canceling old payment waiting orders scheduled task started");
        cancelOldPaymentWaitingOrdersUseCase.execute();
    }
}