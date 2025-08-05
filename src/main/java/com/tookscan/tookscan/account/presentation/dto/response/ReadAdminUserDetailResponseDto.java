package com.tookscan.tookscan.account.presentation.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.tookscan.tookscan.account.domain.User;
import com.tookscan.tookscan.address.dto.response.AddressResponseDto;
import com.tookscan.tookscan.core.dto.SelfValidating;
import com.tookscan.tookscan.core.utility.DateTimeUtil;
import com.tookscan.tookscan.security.domain.type.ESecurityProvider;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
public class ReadAdminUserDetailResponseDto extends SelfValidating<ReadAdminUserDetailResponseDto> {

    @JsonProperty("sign_up_date")
    @Schema(description = "가입 날짜", example = "2023.10.01 12:00")
    private final String signUpDate;

    @JsonProperty("serial_id")
    @Schema(description = "시리얼 ID", example = "user123")
    private final String serialId;

    @JsonProperty("name")
    @Schema(description = "이름", example = "홍길동")
    @NotNull
    private final String name;

    @JsonProperty("provider")
    @Schema(description = "제공자", example = "KAKAO | GOOGLE | DEFAULT")
    @NotNull
    private final ESecurityProvider provider;

    @JsonProperty("phone_number")
    @Schema(description = "전화번호", example = "01012345678")
    @NotNull
    private final String phoneNumber;

    @JsonProperty("email")
    @Schema(description = "이메일", example = "user@example.com")
    private final String email;

    @JsonProperty("address")
    @Schema(description = "주소")
    private final AddressResponseDto address;

    @JsonProperty("delivery_request")
    @Schema(description = "배송 요청사항", example = "문 앞에 두고 가주세요.")
    private final String deliveryRequest;

    @JsonProperty("memo")
    @Schema(description = "메모", example = "VIP 고객")
    private final String memo;

    @JsonProperty("total_payment_amount")
    @Schema(description = "총 결제 금액", example = "100000")
    private final Integer totalPaymentAmount;

    @JsonProperty("total_order_count")
    @Schema(description = "총 주문 수", example = "5")
    private final Integer totalOrderCount;

    @JsonProperty("total_order_document_count")
    @Schema(description = "총 주문 문서 수", example = "10")
    private final Integer totalOrderDocumentCount;

    @JsonProperty("gender")
    @Schema(description = "성별", example = "남성")
    private final String gender;

    @JsonProperty("birth")
    @Schema(description = "출생년도", example = "1990년")
    private final String birth;

    @JsonProperty("is_deleted")
    @Schema(description = "탈퇴 여부", example = "true")
    private final Boolean isDeleted;

    @JsonProperty("deleted_at")
    @Schema(description = "탈퇴 날짜", example = "yyyy.MM.dd HH:mm")
    private final String deletedAt;

    @JsonProperty("reason_deletion")
    @Schema(description = "탈퇴 사유", example = "개인 정보 보호 요청")
    private final String reasonDeletion;



    @Builder
    public ReadAdminUserDetailResponseDto(String signUpDate, String serialId, String name, ESecurityProvider provider,
                                          String phoneNumber,
                                          String email, AddressResponseDto address, String deliveryRequest, String memo,
                                          Integer totalPaymentAmount, Integer totalOrderCount,
                                          Integer totalOrderDocumentCount,
                                          String gender, String birth, LocalDateTime deletedAt, String reasonDeletion,
                                          Boolean isDeleted
                                          ) {
        this.signUpDate = signUpDate;
        this.serialId = serialId;
        this.name = name;
        this.provider = provider;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.address = address;
        this.deliveryRequest = deliveryRequest;
        this.memo = memo;
        this.totalPaymentAmount = totalPaymentAmount;
        this.totalOrderCount = totalOrderCount;
        this.totalOrderDocumentCount = totalOrderDocumentCount;
        this.gender = gender != null ? gender : " - ";
        this.birth = birth != null ? birth : " - ";
        this.isDeleted = isDeleted;
        this.deletedAt = deletedAt != null ? DateTimeUtil.convertLocalDateTimeToDartStringWithoutSecond(deletedAt) : " - ";
        this.reasonDeletion = reasonDeletion != null ? reasonDeletion : " - ";
        this.validateSelf();
    }

    public static ReadAdminUserDetailResponseDto fromEntity(User user) {
        return ReadAdminUserDetailResponseDto.builder()
                .signUpDate(DateTimeUtil.convertLocalDateTimeToDartStringWithoutSecond(user.getCreatedAt()))
                .serialId(user.getSerialId())
                .name(user.getName())
                .provider(user.getProvider())
                .phoneNumber(user.getPhoneNumber())
                .email(user.getEmail() != null ? user.getEmail() : null)
                .address(user.getAddress() != null ? AddressResponseDto.fromEntity(user.getAddress()) : null)
                .deliveryRequest(user.getDeliveryRequest() != null ? user.getDeliveryRequest() : null)
                .memo(user.getMemo() != null ? user.getMemo() : null)
                .totalPaymentAmount(
                        user.getOrders().stream()
                                .mapToInt(order ->
                                        order.getPayment() != null
                                                ? order.getPayment().getTotalAmount()
                                                : 0
                                )
                                .sum()
                )
                .totalOrderCount(user.getOrders().size())
                .totalOrderDocumentCount((int) user.getOrders().stream()
                        .mapToLong(order -> order.getDocuments().size())
                        .sum())
                .gender(user.getGender())
                .birth(user.getBirth())
                .isDeleted(user.getDeletedAt() != null)
                .deletedAt(user.getDeletedAt())
                .reasonDeletion(user.getReasonDeletion())
                .build();
    }
}
