package com.tookscan.tookscan.core.listener;

import com.tookscan.tookscan.core.utility.SmsUtil;
import com.tookscan.tookscan.security.event.CompletePhoneNumberValidationEvent;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class SmsListener {

    private final SmsUtil smsUtil;

    @Async("notificationTaskExecutor")
    @EventListener(classes = {CompletePhoneNumberValidationEvent.class})
    public void handleCompletePhoneNumberValidationEvent(CompletePhoneNumberValidationEvent event) {
        log.atDebug()
            .addKeyValue("receiver_address", event.receiverAddress())
            .addKeyValue("authentication_code", event.authenticationCode())
            .log("[SMS] 휴대폰 인증 완료 이벤트 수신");

        try {
            log.atInfo()
                .addKeyValue("receiver_address", event.receiverAddress())
                .log("[SMS] phone number validation authentication code sent process started");

            smsUtil.sendAuthenticationCode(
                    event.receiverAddress(),
                    event.authenticationCode()
            );

            log.atInfo()
                .addKeyValue("receiver_address", event.receiverAddress())
                .log("[SMS] phone number validation authentication code sent successfully");
        } catch (Exception e) {
            log.atError()
                .setCause(e)
                .addKeyValue("receiver_address", event.receiverAddress())
                .log("[SMS] phone number validation authentication code sending failed");
        }
    }
}
