package com.tookscan.tookscan.core.utility;

import net.nurigo.sdk.NurigoApp;
import net.nurigo.sdk.message.model.KakaoOption;
import net.nurigo.sdk.message.model.Message;
import net.nurigo.sdk.message.request.SingleMessageSendingRequest;
import net.nurigo.sdk.message.service.DefaultMessageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;

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

    @Value("${solapi.template-id.cancel-payment}")
    private String templateIdCancelPayment;

    @Value("${solapi.sender}")
    private String sender;

    @Value("${solapi.tip-url}")
    private String tipUrl;

    @Value("${solapi.payment-url}")
    private String paymentUrl;

    @Value("${solapi.order-detail-url}")
    private String orderDetailUrl;

    @Value("${solapi.order-waybill-url}")
    private String orderWaybillUrl;

    @Value("${solapi.user-post-way-pc-url}")
    private String userPostWayPcUrl;

    @Value("${solapi.user-post-way-mobile-url}")
    private String userPostWayMobileUrl;

    @Value("${solapi.user-post-way-2-url}")
    private String userPostWay2Url;

    @Value("${solapi.post-price-url}")
    private String postPriceUrl;

    public KakaoMessageUtil(
            @Value("${solapi.api-key}") String apiKey,
            @Value("${solapi.api-secret}") String apiSecret,
            @Value("${solapi.url}") String url
    ) {
        this.messageService = NurigoApp.INSTANCE.initialize(apiKey, apiSecret, url);
    }

    public void sendCreateOrderMessage(String userName, String userPhone, String orderName, String to) {

        KakaoOption kakaoOption = new KakaoOption();

        HashMap<String, String> variables = new HashMap<>();
        variables.put("#{userName}", userName);
        variables.put("#{userPhone}", userPhone);
        variables.put("#{orderName}", orderName);
        variables.put("#{userPostWayPc}", userPostWayPcUrl);
        variables.put("#{userPostWayMobile}", userPostWayMobileUrl);
        variables.put("#{userPostWay2}", userPostWay2Url);
        variables.put("#{postPrice}", postPriceUrl);
        variables.put("#{tipUrl}", tipUrl);

        kakaoOption.setVariables(variables);

        kakaoOption.setPfId(pfId);
        kakaoOption.setTemplateId(templateIdCreateOrder);

        Message message = new Message();
        message.setTo(to);
        message.setFrom(sender);
        message.setKakaoOptions(kakaoOption);

        this.messageService.sendOne(new SingleMessageSendingRequest(message));

    }

    public void sendRequestPaymentMessage(String orderName, Integer orderPrice, Long orderId, String orderNumber, String email, String userName, String to) {

        KakaoOption kakaoOption = new KakaoOption();

        HashMap<String, String> variables = new HashMap<>();

        variables.put("#{orderName}", orderName);
        variables.put("#{orderPrice}", String.valueOf(orderPrice));
        variables.put("#{paymentUrl}", paymentUrl + orderId + "?order-number=" + orderNumber + "&payment-total=" + orderPrice + "&order-name=" + orderName + "&order-id=" + orderId + "&email=" + email + "&phone-number=" + to + "&user-name=" + userName);
        variables.put("#{orderDetailUrl}", orderDetailUrl + orderId);

        kakaoOption.setVariables(variables);

        kakaoOption.setPfId(pfId);
        kakaoOption.setTemplateId(templateIdRequestPayment);

        Message message = new Message();
        message.setTo(to);
        message.setFrom(sender);
        message.setKakaoOptions(kakaoOption);

        this.messageService.sendOne(new SingleMessageSendingRequest(message));

    }

    public void sendRequestScanMessage(String orderName, Long orderId, String userEmail, String to) {

        KakaoOption kakaoOption = new KakaoOption();

        HashMap<String, String> variables = new HashMap<>();

        variables.put("#{orderName}", orderName);
        variables.put("#{userEmail}", userEmail);
        variables.put("#{orderDetailUrl}", orderDetailUrl + orderId);

        kakaoOption.setVariables(variables);

        kakaoOption.setPfId(pfId);
        kakaoOption.setTemplateId(templateIdRequestScan);

        Message message = new Message();
        message.setTo(to);
        message.setFrom(sender);
        message.setKakaoOptions(kakaoOption);

        this.messageService.sendOne(new SingleMessageSendingRequest(message));
    }

    public void sendAnnounceScanFinishMessage(String userEmail, String orderName, String to) {

        KakaoOption kakaoOption = new KakaoOption();

        HashMap<String, String> variables = new HashMap<>();
        variables.put("#{orderName}", orderName);
        variables.put("#{userEmail}", userEmail);
        kakaoOption.setVariables(variables);

        kakaoOption.setPfId(pfId);
        kakaoOption.setTemplateId(templateIdAnnounceScanFinish);

        Message message = new Message();
        message.setTo(to);
        message.setFrom(sender);
        message.setKakaoOptions(kakaoOption);

        this.messageService.sendOne(new SingleMessageSendingRequest(message));
    }

    public void sendAnnounceDeliveryMessage(String orderName, String orderWaybill, Long deliveryId, String to) {

        KakaoOption kakaoOption = new KakaoOption();

        String waybillUrl = orderWaybillUrl.replace("{deliveryId}", String.valueOf(deliveryId));

        HashMap<String, String> variables = new HashMap<>();
        variables.put("#{orderName}", orderName);
        variables.put("#{orderWaybill}", orderWaybill);
        variables.put("#{orderDetailUrl}", orderDetailUrl + deliveryId);
        variables.put("#{orderWaybillUrl}", waybillUrl + orderWaybill);

        kakaoOption.setVariables(variables);

        kakaoOption.setPfId(pfId);
        kakaoOption.setTemplateId(templateIdAnnounceDelivery);

        Message message = new Message();
        message.setTo(to);
        message.setFrom(sender);
        message.setKakaoOptions(kakaoOption);

        this.messageService.sendOne(new SingleMessageSendingRequest(message));
    }

    public void sendCancelPaymentMessage(String orderName, String to) {

        KakaoOption kakaoOption = new KakaoOption();

        HashMap<String, String> variables = new HashMap<>();
        variables.put("#{orderName}", orderName);

        kakaoOption.setVariables(variables);

        kakaoOption.setPfId(pfId);
        kakaoOption.setTemplateId(templateIdCancelPayment);

        Message message = new Message();
        message.setTo(to);
        message.setFrom(sender);
        message.setKakaoOptions(kakaoOption);

        this.messageService.sendOne(new SingleMessageSendingRequest(message));
    }
}
