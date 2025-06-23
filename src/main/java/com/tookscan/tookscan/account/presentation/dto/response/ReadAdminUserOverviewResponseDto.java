package com.tookscan.tookscan.account.presentation.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.tookscan.tookscan.account.domain.User;
import com.tookscan.tookscan.account.domain.UserGroup;
import com.tookscan.tookscan.core.dto.PageInfoDto;
import com.tookscan.tookscan.core.dto.SelfValidating;
import com.tookscan.tookscan.core.utility.DateTimeUtil;
import com.tookscan.tookscan.order.domain.Order;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;

@Getter
public class ReadAdminUserOverviewResponseDto extends SelfValidating<ReadAdminUserOverviewResponseDto> {

    @JsonProperty("users")
    @Schema(description = "사용자 목록")
    @NotNull
    private final List<UserOverviewDto> users;

    @JsonProperty("page_info")
    @Schema(description = "페이지 정보")
    @NotNull
    private final PageInfoDto pageInfo;

    @Builder
    public ReadAdminUserOverviewResponseDto(List<UserOverviewDto> users, PageInfoDto pageInfo) {
        this.users = users;
        this.pageInfo = pageInfo;
        this.validateSelf();
    }

    @Getter
    public static class UserOverviewDto extends SelfValidating<UserOverviewDto> {

        @JsonProperty("id")
        @Schema(description = "사용자 ID", example = "d290f1ee-6c54-4b01-90e6-d701748f0851")
        @NotNull
        private final UUID id;

        @JsonProperty("serial_id")
        @Schema(description = "시리얼 ID", example = "user123")
        private final String serialId;

        @JsonProperty("name")
        @Schema(description = "이름", example = "홍길동")
        @NotNull
        private final String name;

        @JsonProperty("phone_number")
        @Schema(description = "전화번호", example = "01012345678")
        @NotNull
        private final String phoneNumber;

        @JsonProperty("email")
        @Schema(description = "이메일", example = "user@example.com")
        private final String email;

        @JsonProperty("sign_in_date")
        @Schema(description = "가입 날짜", example = "2024.01.01")
        @NotNull
        private final String signInDate;

        @JsonProperty("total_order_amount")
        @Schema(description = "총 주문 금액", example = "100000")
        private final Integer totalOrderAmount;

        @JsonProperty("total_order_count")
        @Schema(description = "총 주문 수", example = "5")
        private final Integer totalOrderCount;

        @JsonProperty("total_order_document_count")
        @Schema(description = "총 주문 문서 수", example = "10")
        private final Integer totalOrderDocumentCount;

        @JsonProperty("memo")
        @Schema(description = "메모", example = "VIP 고객")
        private final String memo;

        @JsonProperty("provider")
        @Schema(description = "계정 제공자", example = "KAKAO", allowableValues = {"DEFAULT", "KAKAO", "GOOGLE", "NAVER"})
        private final String provider;

        @JsonProperty("group_infos")
        @Schema(description = "그룹 정보 목록")
        private final GroupInfoListDto groupInfos;

        @Builder
        public UserOverviewDto(UUID id, String serialId, String name, String phoneNumber, String email,
                               String signInDate, Integer totalOrderAmount, Integer totalOrderCount,
                               Integer totalOrderDocumentCount, String memo, String provider,
                               GroupInfoListDto groupInfos) {
            this.id = id;
            this.serialId = serialId;
            this.name = name;
            this.phoneNumber = phoneNumber;
            this.email = email;
            this.signInDate = signInDate;
            this.totalOrderAmount = totalOrderAmount;
            this.totalOrderCount = totalOrderCount;
            this.totalOrderDocumentCount = totalOrderDocumentCount;
            this.memo = memo;
            this.provider = provider;
            this.groupInfos = groupInfos;
            this.validateSelf();
        }

        public static UserOverviewDto of(User user, List<Order> orders) {
            return UserOverviewDto.builder()
                    .id(user.getId())
                    .serialId(user.getSerialId())
                    .name(user.getName())
                    .phoneNumber(user.getPhoneNumber())
                    .email(user.getEmail())
                    .signInDate(DateTimeUtil.convertLocalDateTimeToDartStringWithoutSecond(user.getCreatedAt()))
                    .totalOrderAmount(
                            orders.stream()
                                    .filter(order -> order.getUser().getId().equals(user.getId()))
                                    .mapToInt(Order::getTotalAmount).sum())
                    .totalOrderCount((int) orders.stream()
                            .filter(order -> order.getUser().getId().equals(user.getId()))
                            .count())
                    .totalOrderDocumentCount((int) orders.stream()
                            .filter(order -> order.getUser().getId().equals(user.getId()))
                            .mapToLong(order -> order.getDocuments().size())
                            .sum())
                    .memo(user.getMemo())
                    .provider(user.getProvider().name())
                    .groupInfos(GroupInfoListDto.of(user))
                    .build();
        }
    }

    @Getter
    public static class GroupInfoListDto extends SelfValidating<GroupInfoListDto> {

        @JsonProperty("total_count")
        @Schema(description = "전체 그룹 수", example = "3")
        @NotNull
        private final Integer totalCount;

        @JsonProperty("groups")
        @Schema(description = "그룹 정보 목록")
        private final List<GroupInfoDto> groups;

        @Builder
        public GroupInfoListDto(Integer totalCount, List<GroupInfoDto> groups) {
            this.totalCount = totalCount;
            this.groups = groups;
            this.validateSelf();
        }

        public static GroupInfoListDto of(User user) {

            return GroupInfoListDto.builder()
                    .totalCount(user.getUserGroups().size())
                    .groups(
                            user.getUserGroups().stream()
                                    .map(GroupInfoDto::fromEntity)
                                    .toList()
                    )
                    .build();
        }
    }

    @Getter
    public static class GroupInfoDto extends SelfValidating<GroupInfoDto> {

        @JsonProperty("id")
        @Schema(description = "그룹 ID", example = "1")
        @NotNull
        private final String id;

        @JsonProperty("name")
        @Schema(description = "그룹 이름", example = "관리자")
        @NotNull
        private final String name;

        @Builder
        public GroupInfoDto(String id, String name) {
            this.id = id;
            this.name = name;
            this.validateSelf();
        }

        public static GroupInfoDto fromEntity(UserGroup userGroup) {
            return GroupInfoDto.builder()
                    .id(userGroup.getGroup().getId().toString())
                    .name(userGroup.getGroup().getName())
                    .build();
        }
    }

    public static ReadAdminUserOverviewResponseDto of(List<User> users, List<Order> orders, PageInfoDto pageInfo) {
        return ReadAdminUserOverviewResponseDto.builder()
                .users(users.stream()
                        .map(user -> UserOverviewDto.of(user, orders))
                        .toList())
                .pageInfo(pageInfo)
                .build();
    }
}
