package com.tookscan.tookscan.order.application.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.tookscan.tookscan.core.dto.SelfValidating;
import com.tookscan.tookscan.core.utility.DateTimeUtil;
import com.tookscan.tookscan.order.domain.Order;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

@Getter
public class ReadGuestOrderSummaryResponseDto extends SelfValidating<ReadGuestOrderSummaryResponseDto> {

    @JsonProperty("order_number")
    @NotNull
    private final String orderNumber;

    @JsonProperty("order_date")
    @NotNull
    private final String orderDate;

    @JsonProperty("receiver_name")
    private final String receiverName;

    @JsonProperty("document_description")
    @NotNull
    private final String documentDescription;

    @JsonProperty("payment_prediction")
    @Min(0)
    private final Integer paymentPrediction;

    @JsonProperty("address")
    private final String address;

    @Builder
    public ReadGuestOrderSummaryResponseDto(String orderNumber, String orderDate, String receiverName,
                                            String documentDescription, Integer paymentPrediction, String address) {
        this.orderNumber = orderNumber;
        this.orderDate = orderDate;
        this.receiverName = receiverName;
        this.documentDescription = documentDescription;
        this.paymentPrediction = paymentPrediction;
        this.address = address;
        this.validateSelf();
    }

    public static ReadGuestOrderSummaryResponseDto fromEntity(Order order) {
        return ReadGuestOrderSummaryResponseDto.builder()
                .orderNumber(order.getOrderNumber())
                .orderDate(DateTimeUtil.convertLocalDateTimeToKOREString(order.getCreatedAt()))
                .receiverName(order.getDelivery().getReceiverName())
                .documentDescription(order.getDocumentsDescription())
                .paymentPrediction(order.getTotalAmount())
                .address(order.getDelivery().getAddress().getFullAddress())
                .build();
    }
}
