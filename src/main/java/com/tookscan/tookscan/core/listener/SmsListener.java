package com.tookscan.tookscan.core.listener;

import com.tookscan.tookscan.core.utility.SmsUtil;
import com.tookscan.tookscan.security.event.CompletePhoneNumberValidationEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class SmsListener {

    private final SmsUtil smsUtil;

    @Async("notificationTaskExecutor")
    @EventListener(classes = {CompletePhoneNumberValidationEvent.class})
    public void handleCompletePhoneNumberValidationEvent(CompletePhoneNumberValidationEvent event) {
        log.info(
                "\n----------------------------------\n[ 휴대폰 인증 완료 이벤트 처리 ]\n{}\n{}\n----------------------------------",
                event.receiverAddress() + "님의 휴대폰 인증이 완료되었습니다.",
                "인증코드는 " + event.authenticationCode() + " 입니다."
        );

        try {
            smsUtil.sendAuthenticationCode(
                    event.receiverAddress(),
                    event.authenticationCode()
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
