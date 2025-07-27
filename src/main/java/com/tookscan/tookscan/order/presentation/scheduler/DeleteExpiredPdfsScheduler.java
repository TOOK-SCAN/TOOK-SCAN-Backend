package com.tookscan.tookscan.order.presentation.scheduler;

import com.tookscan.tookscan.order.application.usecase.DeleteExpiredPdfsUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DeleteExpiredPdfsScheduler {

    private final DeleteExpiredPdfsUseCase deleteExpiredPdfsUseCase;

    @Scheduled(cron = "0 0 0 * * ?", zone = "Asia/Seoul")
    public void deleteExpiredPdfs() {
        log.info("Delete expired PDFs scheduled task started");
        deleteExpiredPdfsUseCase.execute();
        log.info("Delete expired PDFs scheduled task completed");
    }
}