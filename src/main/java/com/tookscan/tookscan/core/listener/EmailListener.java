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

@Slf4j
@Component
@RequiredArgsConstructor
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
            e.printStackTrace();
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
            e.printStackTrace();
        }
    }


    @Async("emailTaskExecutor")
    @EventListener(classes = {ChangePasswordBySystemEvent.class})
    public void handleChangePasswordBySystemEvent(ChangePasswordBySystemEvent event) {
        log.info(
                "\n----------------------------------\n[ 임시 비밀번호 발급 이벤트 처리 ]\n{}\n{}\n----------------------------------",
                event.receiverAddress() + "님의 임시 비밀번호가 발급되었습니다.",
                "임시 비밀번호는 " + event.temporaryPassword() + " 입니다."
        );

        try {
            mailUtil.sendTemporaryPassword(
                    event.receiverAddress(),
                    event.temporaryPassword()
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
