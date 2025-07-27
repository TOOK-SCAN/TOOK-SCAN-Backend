package com.tookscan.tookscan.payment.presentation.controller.command;

import com.tookscan.tookscan.core.annotation.swagger.ApiErrorCode;
import com.tookscan.tookscan.core.dto.ResponseDto;
import com.tookscan.tookscan.core.exception.error.ErrorCode;
import com.tookscan.tookscan.payment.presentation.dto.request.ConfirmPaymentRequestDto;
import com.tookscan.tookscan.payment.application.usecase.ConfirmPaymentUseCase;
import com.tookscan.tookscan.payment.presentation.dto.response.ConfirmPaymentResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Payment", description = "Payment 관련 API 입니다.")
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/payments")
public class PaymentCommandV1Controller {
    private final ConfirmPaymentUseCase confirmPaymentUseCase;

    /**
     * 7.2.1 결제 승인
     */
    @Operation(summary = "결제 승인", description = "토스페이먼츠를 통해 결제를 승인합니다.")
    @ApiErrorCode({
        ErrorCode.NOT_FOUND_ORDER,
        ErrorCode.NOT_FOUND_PAYMENT_SESSION,
        ErrorCode.PAYMENT_INCOMPLETE,
        ErrorCode.ALREADY_PROCESSED_PAYMENT,
        ErrorCode.PROVIDER_ERROR,
        ErrorCode.INVALID_API_KEY,
        ErrorCode.UNAUTHORIZED_KEY,
        ErrorCode.REJECT_ACCOUNT_PAYMENT,
        ErrorCode.REJECT_CARD_PAYMENT,
        ErrorCode.REJECT_CARD_COMPANY,
        ErrorCode.INVALID_REJECT_CARD,
        ErrorCode.BELOW_MINIMUM_AMOUNT,
        ErrorCode.EXCEED_MAX_PAYMENT_AMOUNT,
        ErrorCode.EXCEED_MAX_CARD_INSTALLMENT_PLAN,
        ErrorCode.NOT_ALLOWED_POINT_USE,
        ErrorCode.INVALID_CARD_EXPIRATION,
        ErrorCode.INVALID_STOPPED_CARD,
        ErrorCode.EXCEED_MAX_DAILY_PAYMENT_COUNT,
        ErrorCode.NOT_SUPPORTED_INSTALLMENT_PLAN_CARD_OR_MERCHANT,
        ErrorCode.INVALID_CARD_INSTALLMENT_PLAN,
        ErrorCode.NOT_SUPPORTED_MONTHLY_INSTALLMENT_PLAN,
        ErrorCode.EXCEED_MAX_MONTHLY_PAYMENT_AMOUNT,
        ErrorCode.NOT_FOUND_TERMINAL_ID,
        ErrorCode.INVALID_AUTHORIZE_AUTH,
        ErrorCode.INVALID_CARD_LOST_OR_STOLEN,
        ErrorCode.RESTRICTED_TRANSFER_ACCOUNT,
        ErrorCode.INVALID_CARD_NUMBER,
        ErrorCode.INVALID_UNREGISTERED_SUBMALL,
        ErrorCode.NOT_REGISTERED_BUSINESS,
        ErrorCode.EXCEED_MAX_ONE_DAY_WITHDRAW_AMOUNT,
        ErrorCode.EXCEED_MAX_ONE_TIME_WITHDRAW_AMOUNT,
        ErrorCode.CARD_PROCESSING_ERROR,
        ErrorCode.EXCEED_MAX_AMOUNT,
        ErrorCode.INVALID_ACCOUNT_INFO_RE_REGISTER,
        ErrorCode.NOT_AVAILABLE_PAYMENT,
        ErrorCode.UNAPPROVED_ORDER_ID,
        ErrorCode.EXTERNAL_SERVER_ERROR,
        ErrorCode.EXTERNAL_SERVER_TIMEOUT,
        ErrorCode.REST_CLIENT_ERROR,
        ErrorCode.UNKNOWN_PAYMENT_ERROR,
        ErrorCode.FAILED_PAYMENT_INTERNAL_SYSTEM_PROCESSING,
        ErrorCode.COMMON_ERROR
    })
    @PostMapping("")
    public ResponseDto<ConfirmPaymentResponseDto> confirmPayment(
            @RequestBody @Valid ConfirmPaymentRequestDto requestDto
    ) {
        return ResponseDto.created(confirmPaymentUseCase.execute(requestDto));
    }
}
