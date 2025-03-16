package com.tookscan.tookscan.order.application.controller.query;

import com.tookscan.tookscan.core.dto.ResponseDto;
import com.tookscan.tookscan.order.application.dto.response.ReadGuestOrderCouponDetailResponseDto;
import com.tookscan.tookscan.order.application.dto.response.ReadGuestOrderDeliveryResponseDto;
import com.tookscan.tookscan.order.application.dto.response.ReadGuestOrderDetailResponseDto;
import com.tookscan.tookscan.order.application.dto.response.ReadGuestOrderSummaryResponseDto;
import com.tookscan.tookscan.order.application.usecase.ReadGuestOrderCouponDetailUseCase;
import com.tookscan.tookscan.order.application.usecase.ReadGuestOrderDeliveryUseCase;
import com.tookscan.tookscan.order.application.usecase.ReadGuestOrderDetailUseCase;
import com.tookscan.tookscan.order.application.usecase.ReadGuestOrderSummaryUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Order", description = "Order 관련 API 입니다.")
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/guests/orders")
public class OrderGuestQueryV1Controller {

    private final ReadGuestOrderDetailUseCase readGuestOrderDetailUseCase;
    private final ReadGuestOrderSummaryUseCase readGuestOrderSummaryUseCase;
    private final ReadGuestOrderDeliveryUseCase readGuestOrderDeliveryUseCase;
    private final ReadGuestOrderCouponDetailUseCase readGuestOrderCouponDetailUseCase;

    /**
     * 4.2.1 비회원 주문 상세 조회
     */
    @Operation(summary = "비회원 주문 상세 조회", description = "비회원이 주문 상세를 조회합니다.")
    @GetMapping(value = "/details")
    public ResponseDto<ReadGuestOrderDetailResponseDto> getGuestOrderDetail(
            @RequestParam(value = "name") String name,
            @RequestParam(value = "order-number") String orderNumber
    ) {
        return ResponseDto.ok(readGuestOrderDetailUseCase.execute(name, orderNumber));
    }

    /**
     * 4.2.17 비회원 주문 요약 조회
     */
    @Operation(summary = "비회원 주문 요약 조회", description = "비회원이 주문 요약을 조회합니다.")
    @GetMapping(value = "/summary")
    public ResponseDto<ReadGuestOrderSummaryResponseDto> getUserOrderSummary(
            @RequestParam(value = "order-number") String orderNumber
    ) {
        return ResponseDto.ok(readGuestOrderSummaryUseCase.execute(orderNumber));
    }

    /**
     * 4.2.18 비회원 상세 배송 정보 조회
     */
    @Operation(summary = "비회원 상세 배송 정보 조회", description = "비회원이 상세 배송 정보를 조회합니다.")
    @GetMapping(value = "/{orderId}/delivery")
    public ResponseDto<ReadGuestOrderDeliveryResponseDto> getUserOrderDelivery(
            @PathVariable Long orderId
    ) {
        return ResponseDto.ok(readGuestOrderDeliveryUseCase.execute(orderId));
    }

    /**
     * 4.2.20 비회원 쿠폰 정보 조회
     */
    @Operation(summary = "회원 쿠폰 정보 조회", description = "회원이 쿠폰 정보를 조회합니다.")
    @GetMapping(value = "/coupon/detail")
    public ResponseDto<ReadGuestOrderCouponDetailResponseDto> getUserCouponDetail(
            @RequestParam(value = "coupon-code") String couponCode
    ) {
        return ResponseDto.ok(readGuestOrderCouponDetailUseCase.execute(couponCode));
    }

}
