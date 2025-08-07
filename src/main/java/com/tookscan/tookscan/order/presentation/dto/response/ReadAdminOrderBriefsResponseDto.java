package com.tookscan.tookscan.order.presentation.dto.response;

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

    @JsonProperty("scan_in_progress_count")
    @NotNull
    @Min(0)
    private final Integer scanInProgressCount;

    @JsonProperty("scan_completed_count")
    @NotNull
    @Min(0)
    private final Integer scanCompletedCount;

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

    @JsonProperty("all_completed_count")
    @NotNull
    @Min(0)
    private final Integer allCompletedCount;

    @JsonProperty("as_count")
    @NotNull
    @Min(0)
    private final Integer asCount;

    @Builder
    public ReadAdminOrderBriefsResponseDto(Integer totalCount, Integer applyCompletedCount, Integer companyArrivedCount,
                                           Integer paymentWaitingCount, Integer paymentCompletedCount,
                                           Integer scanInProgressCount, Integer scanCompletedCount,
                                           Integer recoveryInProgressCount,
                                           Integer postWaitingCount, Integer cancelCount,
                                           Integer allCompletedCount, Integer asCount) {
        this.totalCount = totalCount;
        this.applyCompletedCount = applyCompletedCount;
        this.companyArrivedCount = companyArrivedCount;
        this.paymentWaitingCount = paymentWaitingCount;
        this.paymentCompletedCount = paymentCompletedCount;
        this.scanInProgressCount = scanInProgressCount;
        this.scanCompletedCount = scanCompletedCount;
        this.recoveryInProgressCount = recoveryInProgressCount;
        this.postWaitingCount = postWaitingCount;
        this.cancelCount = cancelCount;
        this.allCompletedCount = allCompletedCount;
        this.asCount = asCount;
        this.validateSelf();
    }

    public static ReadAdminOrderBriefsResponseDto of(Map<EOrderStatus, Integer> counts,
                                                     Integer asCount) {
        return ReadAdminOrderBriefsResponseDto.builder()
                .totalCount(counts.values().stream().mapToInt(Integer::intValue).sum())
                .applyCompletedCount(counts.get(EOrderStatus.APPLY_COMPLETED))
                .companyArrivedCount(counts.get(EOrderStatus.COMPANY_ARRIVED))
                .paymentWaitingCount(counts.get(EOrderStatus.PAYMENT_WAITING))
                .paymentCompletedCount(counts.get(EOrderStatus.PAYMENT_COMPLETED))
                .scanInProgressCount(counts.get(EOrderStatus.SCAN_IN_PROGRESS))
                .scanCompletedCount(counts.get(EOrderStatus.SCAN_COMPLETED))
                .recoveryInProgressCount(counts.get(EOrderStatus.RECOVERY_IN_PROGRESS))
                .postWaitingCount(counts.get(EOrderStatus.POST_WAITING))
                .cancelCount(counts.get(EOrderStatus.CANCEL))
                .allCompletedCount(counts.get(EOrderStatus.ALL_COMPLETED))
                .asCount(asCount)
                .build();
    }

}
