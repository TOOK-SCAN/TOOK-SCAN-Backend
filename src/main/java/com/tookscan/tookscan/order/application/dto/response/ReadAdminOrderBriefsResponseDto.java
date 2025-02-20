package com.tookscan.tookscan.order.application.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.tookscan.tookscan.core.dto.SelfValidating;
import com.tookscan.tookscan.order.domain.type.EOrderStatus;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.util.Map;
import lombok.Builder;
import lombok.Getter;

@Getter
public class ReadAdminOrderBriefsResponseDto extends SelfValidating<ReadAdminOrderBriefsResponseDto> {

    @JsonProperty("total_count")
    @NotNull
    @Min(0)
    private final Integer totalCount;

    @JsonProperty("apply_completed_count")
    @NotNull
    @Min(0)
    private final Integer applyCompletedCount;

    @JsonProperty("company_arrived_count")
    @NotNull
    @Min(0)
    private final Integer companyArrivedCount;

    @JsonProperty("payment_waiting_count")
    @NotNull
    @Min(0)
    private final Integer paymentWaitingCount;

    @JsonProperty("payment_completed_count")
    @NotNull
    @Min(0)
    private final Integer paymentCompletedCount;

    @JsonProperty("scan_waiting_count")
    @NotNull
    @Min(0)
    private final Integer scanWaitingCount;

    @JsonProperty("scan_in_progress_count")
    @NotNull
    @Min(0)
    private final Integer scanInProgressCount;

    @JsonProperty("recovery_in_progress_count")
    @NotNull
    @Min(0)
    private final Integer recoveryInProgressCount;

    @JsonProperty("post_waiting_count")
    @NotNull
    @Min(0)
    private final Integer postWaitingCount;

    @JsonProperty("cancel_count")
    @NotNull
    @Min(0)
    private final Integer cancelCount;

    @JsonProperty("as_count")
    @NotNull
    @Min(0)
    private final Integer asCount;

    @JsonProperty("all_completed_count")
    @NotNull
    @Min(0)
    private final Integer allCompletedCount;

    @Builder
    public ReadAdminOrderBriefsResponseDto(Integer totalCount, Integer applyCompletedCount, Integer companyArrivedCount, Integer paymentWaitingCount, Integer paymentCompletedCount, Integer scanWaitingCount, Integer scanInProgressCount, Integer recoveryInProgressCount, Integer postWaitingCount, Integer cancelCount, Integer asCount, Integer allCompletedCount) {
        this.totalCount = totalCount;
        this.applyCompletedCount = applyCompletedCount;
        this.companyArrivedCount = companyArrivedCount;
        this.paymentWaitingCount = paymentWaitingCount;
        this.paymentCompletedCount = paymentCompletedCount;
        this.scanWaitingCount = scanWaitingCount;
        this.scanInProgressCount = scanInProgressCount;
        this.recoveryInProgressCount = recoveryInProgressCount;
        this.postWaitingCount = postWaitingCount;
        this.cancelCount = cancelCount;
        this.asCount = asCount;
        this.allCompletedCount = allCompletedCount;
        this.validateSelf();
    }

    public static ReadAdminOrderBriefsResponseDto of(Map<EOrderStatus, Integer> counts){
        return ReadAdminOrderBriefsResponseDto.builder()
                .totalCount(counts.values().stream().mapToInt(Integer::intValue).sum())
                .applyCompletedCount(counts.get(EOrderStatus.APPLY_COMPLETED))
                .companyArrivedCount(counts.get(EOrderStatus.COMPANY_ARRIVED))
                .paymentWaitingCount(counts.get(EOrderStatus.PAYMENT_WAITING))
                .paymentCompletedCount(counts.get(EOrderStatus.PAYMENT_COMPLETED))
                .scanWaitingCount(counts.get(EOrderStatus.SCAN_WAITING))
                .scanInProgressCount(counts.get(EOrderStatus.SCAN_IN_PROGRESS))
                .recoveryInProgressCount(counts.get(EOrderStatus.RECOVERY_IN_PROGRESS))
                .postWaitingCount(counts.get(EOrderStatus.POST_WAITING))
                .cancelCount(counts.get(EOrderStatus.CANCEL))
                .asCount(counts.get(EOrderStatus.AS))
                .allCompletedCount(counts.get(EOrderStatus.ALL_COMPLETED))
                .build();
    }

}
