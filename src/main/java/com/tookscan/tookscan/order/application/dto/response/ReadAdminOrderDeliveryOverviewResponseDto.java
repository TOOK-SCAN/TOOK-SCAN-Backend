package com.tookscan.tookscan.order.application.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.tookscan.tookscan.account.domain.User;
import com.tookscan.tookscan.address.dto.response.AddressResponseDto;
import com.tookscan.tookscan.core.dto.SelfValidating;
import com.tookscan.tookscan.order.domain.Delivery;
import com.tookscan.tookscan.order.domain.Order;
import java.util.Optional;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;

@Getter
public class ReadAdminOrderDeliveryOverviewResponseDto extends
        SelfValidating<ReadAdminOrderDeliveryOverviewResponseDto> {
    @JsonProperty("user_info")
    private final UserInfoDto userInfo;

    @JsonProperty("delivery_info")
    private final DeliveryInfo deliveryInfo;

    @Builder
    public ReadAdminOrderDeliveryOverviewResponseDto(UserInfoDto userInfo, DeliveryInfo deliveryInfo) {
        this.userInfo = userInfo;
        this.deliveryInfo = deliveryInfo;
        this.validateSelf();
    }

    public static ReadAdminOrderDeliveryOverviewResponseDto fromEntity(Order order) {
        return ReadAdminOrderDeliveryOverviewResponseDto.builder()
                .userInfo(Optional.ofNullable(order.getUser())
                        .map(UserInfoDto::fromEntity)
                        .orElse(null))
                .deliveryInfo(DeliveryInfo.fromEntity(order.getDelivery()))
                .build();
    }

    @Getter
    public static class UserInfoDto extends SelfValidating<UserInfoDto> {
        private final UUID id;
        private final String name;
        private final String phoneNumber;
        private final String email;

        @Builder
        public UserInfoDto(UUID id, String name, String phoneNumber, String email) {
            this.id = id;
            this.name = name;
            this.phoneNumber = phoneNumber;
            this.email = email;
            this.validateSelf();
        }

        public static UserInfoDto fromEntity(User user) {
            return UserInfoDto.builder()
                    .id(user.getId())
                    .name(user.getName())
                    .phoneNumber(user.getPhoneNumber())
                    .email(user.getEmail())
                    .build();
        }
    }

    @Getter
    public static class DeliveryInfo extends SelfValidating<DeliveryInfo> {

        @JsonProperty("id")
        private final String id;

        @JsonProperty("receiver_name")
        private final String receiverName;

        @JsonProperty("phone_number")
        private final String phoneNumber;

        @JsonProperty("address")
        private final AddressResponseDto address;

        @JsonProperty("request")
        private final String request;

        @JsonProperty("tracking_number")
        private final String trackingNumber;

        @JsonProperty("email")
        private final String email;

        @Builder
        public DeliveryInfo(String id, String receiverName, String phoneNumber, AddressResponseDto address,
                            String request,
                            String trackingNumber, String email) {
            this.id = id;
            this.receiverName = receiverName;
            this.phoneNumber = phoneNumber;
            this.address = address;
            this.request = request;
            this.trackingNumber = trackingNumber;
            this.email = email;
            this.validateSelf();
        }

        public static DeliveryInfo fromEntity(Delivery delivery) {
            return DeliveryInfo.builder()
                    .id(delivery.getId().toString())
                    .receiverName(delivery.getReceiverName())
                    .phoneNumber(delivery.getPhoneNumber())
                    .address(AddressResponseDto.fromEntity(delivery.getAddress()))
                    .request(delivery.getRequest())
                    .trackingNumber(delivery.getTrackingNumber())
                    .email(delivery.getEmail())
                    .build();
        }
    }
}
