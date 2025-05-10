package com.tookscan.tookscan.order.presentation.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.tookscan.tookscan.core.dto.SelfValidating;
import com.tookscan.tookscan.core.utility.DateTimeUtil;
import com.tookscan.tookscan.order.domain.Order;
import com.tookscan.tookscan.order.domain.type.EDeliveryDetailStatus;
import com.tookscan.tookscan.order.domain.type.EDeliveryStatus;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import lombok.Builder;
import lombok.Getter;

@Getter
public class ReadUserOrderDeliveryResponseDto extends SelfValidating<ReadUserOrderDeliveryResponseDto> {
    @JsonProperty("tracking_number")
    private final String trackingNumber;

    @JsonProperty("carrier_name")
    private final String carrierName;

    @JsonProperty("delivery_status")
    private final EDeliveryStatus deliveryStatus;

    @JsonProperty("events")
    private final List<EventDto> events;

    @Builder
    public ReadUserOrderDeliveryResponseDto(String trackingNumber, String carrierName, EDeliveryStatus deliveryStatus,
                                            List<EventDto> events) {
        this.trackingNumber = trackingNumber;
        this.carrierName = carrierName;
        this.deliveryStatus = deliveryStatus;
        this.events = events;
    }

    public static ReadUserOrderDeliveryResponseDto of(Order order, List<Map<String, Object>> trackingResponse,
                                                      String carrierName) {
        // 마지막 이벤트에서 status.code 값을 가져와서 deliveryStatus 결정
        Map<String, Object> lastEvent = trackingResponse.get(trackingResponse.size() - 1);
        Map<String, Object> statusObject = (Map<String, Object>) lastEvent.get("status");
        EDeliveryDetailStatus statusCode = EDeliveryDetailStatus.fromString((String) statusObject.get("code"));

        return ReadUserOrderDeliveryResponseDto.builder()
                .trackingNumber(order.getDelivery().getTrackingNumber())
                .carrierName(carrierName)
                .deliveryStatus(EDeliveryStatus.getFrom(statusCode))
                .events(trackingResponse.stream().map(EventDto::of).toList())
                .build();
    }

    @Getter
    public static class EventDto {
        @JsonProperty("time")
        private final String time;

        @JsonProperty("status")
        private final StatusDto status;

        @JsonProperty("description")
        private final String description;

        @Builder
        public EventDto(String time, StatusDto statusDto, String description) {
            this.time = time;
            this.status = statusDto;
            this.description = description;
        }

        public static EventDto of(Map<String, Object> event) {
            Map<String, Object> statusObject = (Map<String, Object>) event.get("status");
            EDeliveryDetailStatus statusCode = EDeliveryDetailStatus.fromString((String) statusObject.get("code"));

            // OffsetDateTime을 사용하여 문자열을 파싱한 후 LocalDateTime으로 변환
            LocalDateTime localDateTime = OffsetDateTime.parse((String) event.get("time")).toLocalDateTime();

            return EventDto.builder()
                    .time(DateTimeUtil.convertLocalDateTimeToString(localDateTime))
                    .statusDto(StatusDto.builder()
                            .code(statusCode)
                            .name((String) statusObject.get("name"))
                            .build())
                    .description((String) event.get("description"))
                    .build();
        }

        @Getter
        public static class StatusDto {
            @JsonProperty("code")
            private final EDeliveryDetailStatus code;

            @JsonProperty("name")
            private final String name;

            @Builder
            public StatusDto(EDeliveryDetailStatus code, String name) {
                this.code = code;
                this.name = name;
            }
        }
    }
}