package com.tookscan.tookscan.core.utility;

import com.tookscan.tookscan.core.exception.error.ErrorCode;
import com.tookscan.tookscan.core.exception.type.CommonException;
import com.tookscan.tookscan.security.domain.type.ESecurityRole;
import lombok.extern.slf4j.Slf4j;
import net.nurigo.sdk.NurigoApp;
import net.nurigo.sdk.message.model.KakaoOption;
import net.nurigo.sdk.message.model.Message;
import net.nurigo.sdk.message.request.SingleMessageSendingRequest;
import net.nurigo.sdk.message.response.SingleMessageSentResponse;
import net.nurigo.sdk.message.service.DefaultMessageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;

@Component
@Slf4j
public class KakaoMessageUtil {

    private final DefaultMessageService messageService;

    @Value("${solapi.pf-id}")
    private String pfId;

    @Value("${solapi.template-id.create-order}")
    private String templateIdCreateOrder;

    @Value("${solapi.template-id.request-payment}")
    private String templateIdRequestPayment;

    @Value("${solapi.template-id.request-scan}")
    private String templateIdRequestScan;

    @Value("${solapi.template-id.announce-scan-finish}")
    private String templateIdAnnounceScanFinish;

    @Value("${solapi.template-id.announce-delivery}")
    private String templateIdAnnounceDelivery;

    @Value("${solapi.template-id.thanks-for-using}")
    private String templateIdThanksForUsing;

    @Value("${solapi.path-for-user}")
    private String pathForUser;

    @Value("${solapi.path-for-guest}")
    private String pathForGuest;

    @Value("${solapi.sender}")
    private String sender;

    public KakaoMessageUtil(
            @Value("${solapi.api-key}") String apiKey,
            @Value("${solapi.api-secret}") String apiSecret,
            @Value("${solapi.url}") String url
    ) {
        this.messageService = NurigoApp.INSTANCE.initialize(apiKey, apiSecret, url);
    }

    public void sendCreateOrderMessage(String userName, String orderName, String to) {

        KakaoOption kakaoOption = new KakaoOption();

        HashMap<String, String> variables = new HashMap<>();
        variables.put("#{userName}", userName);
        variables.put("#{orderName}", orderName);

        kakaoOption.setVariables(variables);

        kakaoOption.setPfId(pfId);
        kakaoOption.setTemplateId(templateIdCreateOrder);

        Message message = new Message();
        message.setTo(to);
        message.setFrom(sender);
        message.setKakaoOptions(kakaoOption);

        SingleMessageSentResponse response = this.messageService.sendOne(new SingleMessageSendingRequest(message));

        log.info("Create Order Message Response:\n {}", response);

    }

    public void sendRequestPaymentMessage(ESecurityRole role, String userName, String orderNumber, Long orderId, String to) {

        KakaoOption kakaoOption = new KakaoOption();

        HashMap<String, String> variables = new HashMap<>();

        switch (role) {
            case USER -> {
                variables.put("#{paymentPath}", "로그인 > 마이페이지 > 결제하기");
                variables.put("#{paymentUrl}", pathForUser);
            }
            case GUEST -> {
                variables.put("#{paymentPath}", "비회원 주문조회 > 주문 정보 입력 > 결제하기");
                variables.put("#{paymentUrl}", pathForGuest + "order=" + orderNumber + "$name=" + userName + "$id=" + orderId);
            }
            default -> throw new CommonException(ErrorCode.INVALID_ENUM_TYPE);
        }

        kakaoOption.setVariables(variables);

        kakaoOption.setPfId(pfId);
        kakaoOption.setTemplateId(templateIdRequestPayment);

        Message message = new Message();
        message.setTo(to);
        message.setFrom(sender);
        message.setKakaoOptions(kakaoOption);

        SingleMessageSentResponse response = this.messageService.sendOne(new SingleMessageSendingRequest(message));

        log.info("Request Payment Message Response:\n {}", response);

    }

    public void sendRequestScanMessage(ESecurityRole role, String userName, String orderNumber, String orderName, Long orderId, String to) {

        KakaoOption kakaoOption = new KakaoOption();

        HashMap<String, String> variables = new HashMap<>();

        variables.put("#{orderName}", orderName);

        switch (role) {
            case USER -> {
                variables.put("#{scanPath}", "로그인 > 마이페이지 > 스캔하기");
                variables.put("#{scanUrl}", pathForUser);
            }
            case GUEST -> {
                variables.put("#{scanPath}", "비회원 주문조회 > 주문 정보 입력 > 스캔하기");
                variables.put("#{scanUrl}", pathForGuest + "order=" + orderNumber + "&name=" + userName + "&id=" + orderId);
            }
            default -> throw new CommonException(ErrorCode.INVALID_ENUM_TYPE);
        }

        kakaoOption.setVariables(variables);

        kakaoOption.setPfId(pfId);
        kakaoOption.setTemplateId(templateIdRequestScan);

        Message message = new Message();
        message.setTo(to);
        message.setFrom(sender);
        message.setKakaoOptions(kakaoOption);

        SingleMessageSentResponse response = this.messageService.sendOne(new SingleMessageSendingRequest(message));

        log.info("Request Scan Message Response:\n {}", response);

    }

    public void sendAnnounceScanFinishMessage(String userName, String orderName, String to) {

        KakaoOption kakaoOption = new KakaoOption();

        HashMap<String, String> variables = new HashMap<>();
        variables.put("#{userName}", userName);
        variables.put("#{orderName}", orderName);
        kakaoOption.setVariables(variables);

        kakaoOption.setPfId(pfId);
        kakaoOption.setTemplateId(templateIdAnnounceScanFinish);

        Message message = new Message();
        message.setTo(to);
        message.setFrom(sender);
        message.setKakaoOptions(kakaoOption);

        SingleMessageSentResponse response = this.messageService.sendOne(new SingleMessageSendingRequest(message));

        log.info("Announce Scan Finish Message Response:\n {}", response);

    }

    public void sendAnnounceDeliveryMessage(String to) {

        KakaoOption kakaoOption = new KakaoOption();

        kakaoOption.setPfId(pfId);
        kakaoOption.setTemplateId(templateIdAnnounceDelivery);

        Message message = new Message();
        message.setTo(to);
        message.setFrom(sender);
        message.setKakaoOptions(kakaoOption);

        SingleMessageSentResponse response = this.messageService.sendOne(new SingleMessageSendingRequest(message));

        log.info("Announce Delivery Message Response:\n {}", response);

    }

    public void sendThanksForUsingMessage(String to) {

        KakaoOption kakaoOption = new KakaoOption();

        kakaoOption.setPfId(pfId);
        kakaoOption.setTemplateId(templateIdThanksForUsing);

        Message message = new Message();
        message.setTo(to);
        message.setFrom(sender);
        message.setKakaoOptions(kakaoOption);

        SingleMessageSentResponse response = this.messageService.sendOne(new SingleMessageSendingRequest(message));

        log.info("Thanks For Using Message Response:\n {}", response);

    }

}
