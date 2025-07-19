package com.tookscan.tookscan.order.presentation.controller.query;

import com.tookscan.tookscan.core.annotation.swagger.ApiErrorCode;
import com.tookscan.tookscan.core.dto.ResponseDto;
import com.tookscan.tookscan.core.exception.error.ErrorCode;
import com.tookscan.tookscan.order.application.usecase.EstimateUserOrderPriceUseCase;
import com.tookscan.tookscan.order.presentation.dto.request.EstimateUserOrderPriceRequestDto;
import com.tookscan.tookscan.order.presentation.dto.response.EstimateUserOrderPriceResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Order", description = "Order 관련 API 입니다.")
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/orders")
public class OrderCommonQueryV1Controller {
    private final EstimateUserOrderPriceUseCase estimateUserOrderPriceUseCase;

    /**
     * 4.1.9 스캔 가격 계산
     */
    @Operation(summary = "스캔 가격 계산", description = "스캔 가격을 계산합니다.")
    @ApiErrorCode({
            ErrorCode.NOT_FOUND_PRICE_POLICY,
            ErrorCode.NOT_FOUND_COUPON,
            ErrorCode.NOT_AVAILABLE_COUPON,
            ErrorCode.USED_COUPON,
            ErrorCode.INVALID_ARGUMENT,
            ErrorCode.BAD_REQUEST_PARAMETER
    })
    @PostMapping(value = "/estimate")
    public ResponseDto<EstimateUserOrderPriceResponseDto> estimateOrderPrice(
            @RequestBody @Valid EstimateUserOrderPriceRequestDto requestDto
    ) {
        return ResponseDto.ok(estimateUserOrderPriceUseCase.execute(requestDto));
    }
}
