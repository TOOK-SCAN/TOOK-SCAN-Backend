package com.tookscan.tookscan.order.presentation.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.tookscan.tookscan.core.dto.SelfValidating;
import com.tookscan.tookscan.order.domain.Order;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
public class ReadGuestOrderDetailResponseDto extends SelfValidating<ReadGuestOrderDetailResponseDto> {
    @JsonProperty("id")
    @NotNull
    private final String id;

    @JsonProperty("order_number")
    @NotNull
    private final String orderNumber;

    @JsonProperty("amount")
    @NotNull
    private final Integer amount;

    @JsonProperty("order_name")
    @NotNull
    private final String orderName;

    @JsonProperty("phone_number")
    @NotNull
    private final String phoneNumber;

    @JsonProperty("user_name")
    @NotNull
    private final String userName;

    @JsonProperty("email")
    @NotNull
    private final String email;

    @JsonProperty("customer_key")
    private final UUID customerKey;

    @Builder
    public ReadGuestOrderDetailResponseDto(
            String id,
            String orderNumber,
            Integer amount,
            String orderName,
            String phoneNumber,
            String userName,
            String email,
            UUID customerKey
    ) {
        this.id = id;
        this.orderNumber = orderNumber;
        this.amount = amount;
        this.orderName = orderName;
        this.phoneNumber = phoneNumber;
        this.userName = userName;
        this.email = email;
        this.customerKey = customerKey;

        this.validateSelf();
    }

    public static ReadGuestOrderDetailResponseDto fromEntity(
            Order order
    ) {
        return ReadGuestOrderDetailResponseDto.builder()
                .id(order.getId().toString())
                .orderNumber(order.getOrderNumber())
                .amount(order.getTotalAmount())
                .orderName(order.getDocumentsDescription())
                .phoneNumber(order.getUser().getPhoneNumber())
                .userName(order.getUser().getName())
                .email(order.getUser().getEmail() != null ? order.getUser().getEmail() : order.getDelivery().getEmail())
                .customerKey(order.getUser().getId())
                .build();
    }
}