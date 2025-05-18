package com.tookscan.tookscan.order.presentation.controller.query;

import com.tookscan.tookscan.core.annotation.security.AccountID;
import com.tookscan.tookscan.core.dto.ResponseDto;
import com.tookscan.tookscan.order.application.usecase.ReadUserOrderCouponDetailUseCase;
import com.tookscan.tookscan.order.application.usecase.ReadUserOrderDeliveryUseCase;
import com.tookscan.tookscan.order.application.usecase.ReadUserOrderDetailUseCase;
import com.tookscan.tookscan.order.application.usecase.ReadUserOrderOverviewUseCase;
import com.tookscan.tookscan.order.application.usecase.ReadUserOrderSummaryUseCase;
import com.tookscan.tookscan.order.presentation.dto.response.ReadUserOrderCouponDetailResponseDto;
import com.tookscan.tookscan.order.presentation.dto.response.ReadUserOrderDeliveryResponseDto;
import com.tookscan.tookscan.order.presentation.dto.response.ReadUserOrderDetailResponseDto;
import com.tookscan.tookscan.order.presentation.dto.response.ReadUserOrderOverviewResponseDto;
import com.tookscan.tookscan.order.presentation.dto.response.ReadUserOrderSummaryResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Min;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Order", description = "Order 관련 API 입니다.")
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/users/orders")
public class OrderUserQueryV1Controller {
    private final ReadUserOrderOverviewUseCase readUserOrderOverviewUseCase;
    private final ReadUserOrderDetailUseCase readUserOrderDetailUseCase;
    private final ReadUserOrderSummaryUseCase readUserOrderSummaryUseCase;
    private final ReadUserOrderDeliveryUseCase readUserOrderDeliveryUseCase;
    private final ReadUserOrderCouponDetailUseCase readUserOrderCouponDetailUseCase;

    /**
     * 4.2.2 회원 주문 내역 조회
     */
    @Operation(summary = "회원 주문 내역 조회", description = "회원이 주문 내역을 조회합니다.")
    @GetMapping(value = "/overviews")
    public ResponseDto<ReadUserOrderOverviewResponseDto> getOrderOverview(
            @Parameter(hidden = true) @AccountID UUID accountId,
            @Min(0) @RequestParam(value = "page", defaultValue = "1") int page,
            @Min(0) @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "start-date", required = false) String startDate,
            @RequestParam(value = "end-date", required = false) String endDate,
            @RequestParam(value = "sort", required = false) String sort,
            @RequestParam(value = "search", required = false) String search,
            @RequestParam(value = "direction", required = false, defaultValue = "desc") String direction

    ) {
        return ResponseDto.ok(
                readUserOrderOverviewUseCase.execute(accountId, page, size, sort, search, direction, startDate,
                        endDate));
    }

    /**
     * 4.2.3 회원 주문 상세 조회
     */
    @Operation(summary = "회원 주문 상세 조회", description = "회원이 주문 상세를 조회합니다.")
    @GetMapping(value = "/{orderId}/details")
    public ResponseDto<ReadUserOrderDetailResponseDto> getUserOrderDetail(
            @Parameter(hidden = true) @AccountID UUID accountId,
            @PathVariable Long orderId
    ) {
        return ResponseDto.ok(readUserOrderDetailUseCase.execute(accountId, orderId));
    }

    /**
     * 4.2.4 회원 상세 배송 정보 조회
     */
    @Operation(summary = "회원 상세 배송 정보 조회", description = "회원이 상세 배송 정보를 조회합니다.")
    @GetMapping(value = "/{orderId}/delivery")
    public ResponseDto<ReadUserOrderDeliveryResponseDto> getUserOrderDelivery(
            @Parameter(hidden = true) @AccountID UUID accountId,
            @PathVariable Long orderId
    ) {
        return ResponseDto.ok(readUserOrderDeliveryUseCase.execute(accountId, orderId));
    }

    /**
     * 4.2.16 회원 주문 요약 조회
     */
    @Operation(summary = "회원 주문 요약 조회", description = "회원이 주문 요약을 조회합니다.")
    @GetMapping(value = "/summary")
    public ResponseDto<ReadUserOrderSummaryResponseDto> getUserOrderSummary(
            @Parameter(hidden = true) @AccountID UUID accountId,
            @RequestParam(value = "order-number") String orderNumber
    ) {
        return ResponseDto.ok(readUserOrderSummaryUseCase.execute(accountId, orderNumber));
    }

    /**
     * 4.2.19 회원 쿠폰 정보 조회
     */
    @Operation(summary = "회원 쿠폰 정보 조회", description = "회원이 쿠폰 정보를 조회합니다.")
    @GetMapping(value = "/coupon/detail")
    public ResponseDto<ReadUserOrderCouponDetailResponseDto> getUserCouponDetail(
            @Parameter(hidden = true) @AccountID UUID accountId,
            @RequestParam(value = "coupon-code") String couponCode
    ) {
        return ResponseDto.ok(readUserOrderCouponDetailUseCase.execute(accountId, couponCode));
    }

}
