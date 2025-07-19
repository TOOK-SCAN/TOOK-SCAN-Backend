package com.tookscan.tookscan.payment.presentation.controller.command;

import com.tookscan.tookscan.core.annotation.swagger.ApiErrorCode;
import com.tookscan.tookscan.core.dto.ResponseDto;
import com.tookscan.tookscan.core.exception.error.ErrorCode;
import com.tookscan.tookscan.payment.application.usecase.CreatePaymentUseCase;
import com.tookscan.tookscan.payment.application.usecase.RefundPaymentUseCase;
import com.tookscan.tookscan.payment.presentation.dto.request.CreatePaymentRequestDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Payment", description = "Payment 관련 API 입니다.")
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/admins/payments")
public class PaymentAdminCommandV1Controller {

    private final RefundPaymentUseCase refundPaymentUseCase;
    private final CreatePaymentUseCase createPaymentUseCase;

    /**
     * 7.3.1 (관리자) 환불하기
     */
    @Operation(summary = "관리자 환불하기", description = "관리자가 환불을 진행합니다.")
    @ApiErrorCode({
        ErrorCode.NOT_FOUND_PAYMENT,
        ErrorCode.ALREADY_CANCELED_PAYMENT,
        ErrorCode.NOT_CANCELABLE_PAYMENT,
        ErrorCode.NOT_CANCELABLE_AMOUNT,
        ErrorCode.FORBIDDEN_CONSECUTIVE_REQUEST,
        ErrorCode.PROVIDER_ERROR,
        ErrorCode.EXTERNAL_SERVER_ERROR,
        ErrorCode.EXTERNAL_SERVER_TIMEOUT,
        ErrorCode.REST_CLIENT_ERROR,
        ErrorCode.UNKNOWN_PAYMENT_ERROR,
        ErrorCode.FAILED_PAYMENT_INTERNAL_SYSTEM_PROCESSING,
        ErrorCode.FAILED_REFUND_PROCESS,
        ErrorCode.FAILED_METHOD_HANDLING_CANCEL,
        ErrorCode.FAILED_PARTIAL_REFUND,
        ErrorCode.COMMON_ERROR
    })
    @PatchMapping("/{id}/cancel")
    public ResponseDto<Void> refundPayment(
            @PathVariable Long id
    ) {
        refundPaymentUseCase.execute(id);
        return ResponseDto.ok(null);
    }

    /**
     * 7.3.2 (관리자) 결제 정보 등록
     */
    @Operation(summary = "관리자 결제 정보 등록", description = "관리자가 결제 정보를 등록합니다.")
    @ApiErrorCode(
            ErrorCode.NOT_FOUND_ORDER
    )
    @PostMapping("")
    public ResponseDto<Void> createPayment(
            @RequestBody @Valid CreatePaymentRequestDto requestDto
    ) {
        createPaymentUseCase.execute(requestDto);
        return ResponseDto.created(null);
    }

}
