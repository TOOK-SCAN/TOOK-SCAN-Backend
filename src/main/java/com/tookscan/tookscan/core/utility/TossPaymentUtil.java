package com.tookscan.tookscan.core.utility;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tookscan.tookscan.core.dto.PaymentDto;
import com.tookscan.tookscan.core.dto.PaymentRefundDto;
import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Component
public class TossPaymentUtil {
    @Value("${toss.payments.secret-key}")
    private String tossSecretKey;

    @Value("${toss.payments.confirm-url}")
    private String tossConfirmApiUrl;

    @Value("${toss.payments.refund-url}")
    private String tossRefundApiUrl;

    private ObjectMapper objectMapper = new ObjectMapper();

    public String getTossConfirmRequestUrl() {
        return tossConfirmApiUrl;
    }

    public String getTossRefundRequestUrl(String paymentKey) {
        return tossRefundApiUrl.replace("{paymentKey}", paymentKey);
    }

    public HttpHeaders getTossConfirmRequestHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        String encodedKey = Base64.getEncoder().encodeToString((tossSecretKey + ":").getBytes(StandardCharsets.UTF_8));
        headers.set("Authorization", "Basic " + encodedKey);
        return headers;
    }

    public HttpHeaders getTossRefundRequestHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        String encodedKey = Base64.getEncoder().encodeToString((tossSecretKey + ":").getBytes(StandardCharsets.UTF_8));
        headers.set("Authorization", "Basic " + encodedKey);
        return headers;
    }

    public String createTossConfirmRequestBody(String paymentKey, String orderId, Integer amount) {
        JSONObject payload = new JSONObject();

        payload.put("paymentKey", paymentKey);
        payload.put("orderId", orderId);
        payload.put("amount", amount);

        return payload.toJSONString();
    }

    public String createTossRefundRequestBody() {
        JSONObject payload = new JSONObject();

        payload.put("cancelReason", "관리자 환불 요청");

        return payload.toJSONString();
    }

    public PaymentDto mapToPaymentDto(JSONObject jsonObject) {
        try {
            return objectMapper.readValue(jsonObject.toString(), PaymentDto.class);
        } catch (Exception e) {
            throw new RuntimeException("Failed to map to PaymentDto: " + e.getMessage(), e);
        }
    }

    public PaymentRefundDto mapToPaymentRefundDto(JSONObject jsonObject) {
        try {
            return objectMapper.readValue(jsonObject.toString(), PaymentRefundDto.class);
        } catch (Exception e) {
            throw new RuntimeException("Failed to map to PaymentRefundDto: " + e.getMessage(), e);
        }
    }


}
