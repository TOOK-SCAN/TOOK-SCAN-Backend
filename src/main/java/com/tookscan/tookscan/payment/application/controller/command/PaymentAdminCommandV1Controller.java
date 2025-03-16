package com.tookscan.tookscan.payment.application.controller.command;

import com.tookscan.tookscan.core.dto.ResponseDto;
import com.tookscan.tookscan.payment.application.usecase.RefundPaymentUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Payment", description = "Payment 관련 API 입니다.")
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/admins/payments")
public class PaymentAdminCommandV1Controller {

    private final RefundPaymentUseCase refundPaymentUseCase;

    /**
     * 7.3.1 (관리자) 환불하기
     */
    @Operation(summary = "관리자 환불하기", description = "관리자가 환불을 진행합니다.")
    @PatchMapping("/{id}/cancel")
    public ResponseDto<Void> refundPayment(
            @PathVariable Long id
    ) {
        refundPaymentUseCase.execute(id);
        return ResponseDto.ok(null);
    }

}
