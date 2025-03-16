package com.tookscan.tookscan.core.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public record PaymentRefundDto(

        @JsonProperty("mId")
        String mId,

        @JsonProperty("lastTransactionKey")
        String lastTransactionKey,

        @JsonProperty("paymentKey")
        String paymentKey,

        @JsonProperty("orderId")
        String orderId,

        @JsonProperty("orderName")
        String orderName,

        @JsonProperty("taxExemptionAmount")
        Integer taxExemptionAmount,

        @JsonProperty("status")
        String status,

        @JsonProperty("requestedAt")
        String requestedAt,

        @JsonProperty("approvedAt")
        String approvedAt,

        @JsonProperty("useEscrow")
        Boolean useEscrow,

        @JsonProperty("cultureExpense")
        Boolean cultureExpense,

        @JsonProperty("card")
        CardInfo card,

        @JsonProperty("virtualAccount")
        Object virtualAccount,

        @JsonProperty("transfer")
        Object transfer,

        @JsonProperty("mobilePhone")
        Object mobilePhone,

        @JsonProperty("giftCertificate")
        Object giftCertificate,

        @JsonProperty("cashReceipt")
        Object cashReceipt,

        @JsonProperty("cashReceipts")
        Object cashReceipts,

        @JsonProperty("discount")
        Object discount,

        @JsonProperty("cancels")
        List<CancelInfo> cancels,

        @JsonProperty("secret")
        Object secret,

        @JsonProperty("type")
        String type,

        @JsonProperty("easyPay")
        EasyPayInfo easyPay,

        @JsonProperty("country")
        String country,

        @JsonProperty("failure")
        Object failure,

        @JsonProperty("isPartialCancelable")
        Boolean isPartialCancelable,

        @JsonProperty("receipt")
        ReceiptInfo receipt,

        @JsonProperty("checkout")
        CheckoutInfo checkout,

        @JsonProperty("currency")
        String currency,

        @JsonProperty("totalAmount")
        Integer totalAmount,

        @JsonProperty("balanceAmount")
        Integer balanceAmount,

        @JsonProperty("suppliedAmount")
        Integer suppliedAmount,

        @JsonProperty("vat")
        Integer vat,

        @JsonProperty("taxFreeAmount")
        Integer taxFreeAmount,

        @JsonProperty("metadata")
        Object metadata,

        @JsonProperty("method")
        String method,

        @JsonProperty("version")
        String version
) {

    public record CardInfo(
            @JsonProperty("issuerCode")
            String issuerCode,
            @JsonProperty("acquirerCode")
            String acquirerCode,
            @JsonProperty("number")
            String number,
            @JsonProperty("installmentPlanMonths")
            Integer installmentPlanMonths,
            @JsonProperty("isInterestFree")
            Boolean isInterestFree,
            @JsonProperty("interestPayer")
            String interestPayer,
            @JsonProperty("approveNo")
            String approveNo,
            @JsonProperty("useCardPoint")
            Boolean useCardPoint,
            @JsonProperty("cardType")
            String cardType,
            @JsonProperty("ownerType")
            String ownerType,
            @JsonProperty("acquireStatus")
            String acquireStatus,
            @JsonProperty("amount")
            Integer amount
    ) {}

    public record EasyPayInfo(
            @JsonProperty("provider")
            String provider,
            @JsonProperty("amount")
            Integer amount,
            @JsonProperty("discountAmount")
            Integer discountAmount
    ) {}

    public record ReceiptInfo(
            @JsonProperty("url")
            String url
    ) {}

    public record CheckoutInfo(
            @JsonProperty("url")
            String url
    ) {}

    public record CancelInfo(
            @JsonProperty("transactionKey")
            String transactionKey,
            @JsonProperty("cancelReason")
            String cancelReason,
            @JsonProperty("taxExemptionAmount")
            Integer taxExemptionAmount,
            @JsonProperty("canceledAt")
            String canceledAt,
            @JsonProperty("transferDiscountAmount")
            Integer transferDiscountAmount,
            @JsonProperty("easyPayDiscountAmount")
            Integer easyPayDiscountAmount,
            @JsonProperty("receiptKey")
            String receiptKey,
            @JsonProperty("cancelAmount")
            Integer cancelAmount,
            @JsonProperty("taxFreeAmount")
            Integer taxFreeAmount,
            @JsonProperty("refundableAmount")
            Integer refundableAmount,
            @JsonProperty("cancelStatus")
            String cancelStatus,
            @JsonProperty("cancelRequestId")
            String cancelRequestId
    ) {}
}
