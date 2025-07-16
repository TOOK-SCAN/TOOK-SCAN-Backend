package com.tookscan.tookscan.core.utility;

import java.util.HashMap;
import net.nurigo.sdk.NurigoApp;
import net.nurigo.sdk.message.model.KakaoOption;
import net.nurigo.sdk.message.model.Message;
import net.nurigo.sdk.message.request.SingleMessageSendingRequest;
import net.nurigo.sdk.message.service.DefaultMessageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
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
        variables.put("#{orderName}", "[" + orderName + "]");

        kakaoOption.setVariables(variables);

        kakaoOption.setPfId(pfId);
        kakaoOption.setTemplateId(templateIdCreateOrder);

        Message message = new Message();
        message.setTo(to);
        message.setFrom(sender);
        message.setKakaoOptions(kakaoOption);

        this.messageService.sendOne(new SingleMessageSendingRequest(message));

    }

    public void sendRequestPaymentMessage(String userName, String orderName, String to) {

        KakaoOption kakaoOption = new KakaoOption();

        HashMap<String, String> variables = new HashMap<>();
        variables.put("#{paymentPath}", "로그인 > 마이페이지 > 결제하기");
        variables.put("#{paymentUrl}", pathForUser);
        variables.put("#{userName}", userName);
        variables.put("#{orderName}", "[" + orderName + "]");

        kakaoOption.setVariables(variables);

        kakaoOption.setPfId(pfId);
        kakaoOption.setTemplateId(templateIdRequestPayment);

        Message message = new Message();
        message.setTo(to);
        message.setFrom(sender);
        message.setKakaoOptions(kakaoOption);

        this.messageService.sendOne(new SingleMessageSendingRequest(message));

    }

    public void sendRequestScanMessage(String orderName, String to) {

        KakaoOption kakaoOption = new KakaoOption();

        HashMap<String, String> variables = new HashMap<>();

        variables.put("#{orderName}", "[" + orderName + "]");
        variables.put("#{scanPath}", "로그인 > 마이페이지 > 스캔하기");
        variables.put("#{scanUrl}", pathForUser);

        kakaoOption.setVariables(variables);

        kakaoOption.setPfId(pfId);
        kakaoOption.setTemplateId(templateIdRequestScan);

        Message message = new Message();
        message.setTo(to);
        message.setFrom(sender);
        message.setKakaoOptions(kakaoOption);

        this.messageService.sendOne(new SingleMessageSendingRequest(message));
    }

    public void sendAnnounceScanFinishMessage(String userName, String orderName, String to) {

        KakaoOption kakaoOption = new KakaoOption();

        HashMap<String, String> variables = new HashMap<>();
        variables.put("#{userName}", userName);
        variables.put("#{orderName}", "[" + orderName + "]");
        kakaoOption.setVariables(variables);

        kakaoOption.setPfId(pfId);
        kakaoOption.setTemplateId(templateIdAnnounceScanFinish);

        Message message = new Message();
        message.setTo(to);
        message.setFrom(sender);
        message.setKakaoOptions(kakaoOption);

        this.messageService.sendOne(new SingleMessageSendingRequest(message));
    }

    public void sendAnnounceDeliveryMessage(String to) {

        KakaoOption kakaoOption = new KakaoOption();

        kakaoOption.setPfId(pfId);
        kakaoOption.setTemplateId(templateIdAnnounceDelivery);

        Message message = new Message();
        message.setTo(to);
        message.setFrom(sender);
        message.setKakaoOptions(kakaoOption);

        this.messageService.sendOne(new SingleMessageSendingRequest(message));
    }

    public void sendThanksForUsingMessage(String to) {

        KakaoOption kakaoOption = new KakaoOption();

        kakaoOption.setPfId(pfId);
        kakaoOption.setTemplateId(templateIdThanksForUsing);

        Message message = new Message();
        message.setTo(to);
        message.setFrom(sender);
        message.setKakaoOptions(kakaoOption);

        this.messageService.sendOne(new SingleMessageSendingRequest(message));
    }

}
