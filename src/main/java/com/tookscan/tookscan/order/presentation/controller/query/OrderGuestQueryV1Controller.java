package com.tookscan.tookscan.order.presentation.controller.query;

import com.tookscan.tookscan.core.annotation.swagger.ApiErrorCode;
import com.tookscan.tookscan.core.dto.ResponseDto;
import com.tookscan.tookscan.core.exception.error.ErrorCode;
import com.tookscan.tookscan.order.application.usecase.ReadGuestOrderDetailUseCase;
import com.tookscan.tookscan.order.presentation.dto.response.ReadGuestOrderDetailResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Order", description = "Order 관련 API 입니다.")
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/guests/orders")
public class OrderGuestQueryV1Controller {

    private final ReadGuestOrderDetailUseCase readGuestOrderDetailUseCase;

    /**
     * 4.2.3 회원 주문 상세 조회
     */
    @Operation(summary = "비회원 주문 상세 조회", description = "비회원이 주문 상세를 조회합니다. 재결제를 위해 필요합니다.")
    @ApiErrorCode({
            ErrorCode.NOT_FOUND_ORDER
    })
    @GetMapping(value = "/{orderNumber}/details")
    public ResponseDto<ReadGuestOrderDetailResponseDto> getGuestOrderDetail(
            @PathVariable String orderNumber
    ) {
        return ResponseDto.ok(readGuestOrderDetailUseCase.execute(orderNumber));
    }
}
