package com.tookscan.tookscan.core.listener;

import com.tookscan.tookscan.core.utility.MailUtil;
import com.tookscan.tookscan.mail.domain.event.EmailEvent;
import com.tookscan.tookscan.mail.domain.event.SendPdfEmailEvent;
import com.tookscan.tookscan.security.event.ChangePasswordBySystemEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class EmailListener {

    private final MailUtil mailUtil;

    @Async("emailTaskExecutor")
    @EventListener(classes = {EmailEvent.class})
    public void handleSendTestEmailEvent(EmailEvent event) {
        try {
            mailUtil.sendTestEmail(
                    event.getEmail()
            );
        } catch (Exception e) {
            log.atError()
                .setCause(e)
                .addKeyValue("email", event.getEmail())
                .log("[Email] Failed to send test email");
        }
    }

    @Async("emailTaskExecutor")
    @EventListener(classes = {SendPdfEmailEvent.class})
    public void handleSendPdfEmailEvent(SendPdfEmailEvent event) {
        try {
            mailUtil.sendPdfEmail(
                    event.getEmail(),
                    event.getUserName(),
                    event.getOrderNumber(),
                    event.getOrderName(),
                    event.getPdfUrl()
            );
        } catch (Exception e) {
            log.atError()
                .setCause(e)
                .addKeyValue("email", event.getEmail())
                .addKeyValue("order_number", event.getOrderNumber())
                .log("[Email] Failed to send PDF email");
        }
    }


    @Async("emailTaskExecutor")
    @EventListener(classes = {ChangePasswordBySystemEvent.class})
    public void handleChangePasswordBySystemEvent(ChangePasswordBySystemEvent event) {
        try {
            mailUtil.sendTemporaryPassword(
                    event.receiverAddress(),
                    event.temporaryPassword()
            );
        } catch (Exception e) {
            log.atError()
                .setCause(e)
                .addKeyValue("email", event.receiverAddress())
                .log("[Email] Failed to send temporary password email");
        }
    }
}
