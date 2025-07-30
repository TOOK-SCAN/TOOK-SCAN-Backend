package com.tookscan.tookscan.core.listener;

import com.tookscan.tookscan.core.utility.SmsUtil;
import com.tookscan.tookscan.core.utility.StructuredLoggerUtil;
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
        StructuredLoggerUtil.debug(log)
                .message("[SMS] 휴대폰 인증 완료 이벤트 수신")
                .field("receiver_address", event.receiverAddress())
                .field("authentication_code", event.authenticationCode())
                .log();

        try {
            StructuredLoggerUtil.info(log)
                    .message("[SMS] phone number validation authentication code sent process started")
                    .details(Map.of(
                            "receiver_address", event.receiverAddress()
                    ))
                    .log();

            smsUtil.sendAuthenticationCode(
                    event.receiverAddress(),
                    event.authenticationCode()
            );

            StructuredLoggerUtil.info(log)
                    .message("[SMS] phone number validation authentication code sent successfully")
                    .details(Map.of(
                            "receiver_address", event.receiverAddress()
                    ))
                    .log();
        } catch (Exception e) {
            StructuredLoggerUtil.error(log)
                    .message("[SMS] phone number validation authentication code sending failed")
                    .field("receiver_address", event.receiverAddress())
                    .exception(e)
                    .log();
        }
    }
}
