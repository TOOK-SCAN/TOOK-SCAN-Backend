package com.tookscan.tookscan.order.presentation.controller.command;

import com.tookscan.tookscan.core.annotation.security.AccountID;
import com.tookscan.tookscan.core.dto.ResponseDto;
import com.tookscan.tookscan.order.application.usecase.CreateUserOrderUseCase;
import com.tookscan.tookscan.order.application.usecase.UpdateUserOrderCancelUseCase;
import com.tookscan.tookscan.order.application.usecase.UpdateUserOrderScanUseCase;
import com.tookscan.tookscan.order.presentation.dto.request.CreateUserOrderRequestDto;
import com.tookscan.tookscan.order.presentation.dto.response.CreateUserOrderResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Order", description = "Order 관련 API 입니다.")
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/users/orders")
public class OrderUserCommandV1Controller {
    private final CreateUserOrderUseCase createUserOrderUseCase;
    private final UpdateUserOrderScanUseCase updateUserOrderScanUseCase;
    private final UpdateUserOrderCancelUseCase updateUserOrderCancelUseCase;

    /**
     * 4.1 회원 스캔 주문
     */
    @Operation(summary = "회원 스캔 주문", description = "회원이 주문을 생성합니다.")
    @PostMapping()
    public ResponseDto<CreateUserOrderResponseDto> createOrder(
            @Parameter(hidden = true) @AccountID UUID accountId,
            @RequestBody @Valid CreateUserOrderRequestDto requestDto
    ) {
        return ResponseDto.created(createUserOrderUseCase.execute(accountId, requestDto));
    }

    /**
     * 4.4 회원 스캔하기
     */
    @Operation(summary = "회원 스캔하기", description = "회원이 주문을 스캔합니다.")
    @PatchMapping(value = "/{orderId}/scan")
    public ResponseDto<Void> updateOrderScan(
            @Parameter(hidden = true) @AccountID UUID accountId,
            @PathVariable Long orderId
    ) {
        updateUserOrderScanUseCase.execute(accountId, orderId);
        return ResponseDto.ok(null);
    }

    /**
     * 4.3.9 회원 주문 취소하기
     */
    @Operation(summary = "회원 주문 취소하기", description = "회원이 주문을 취소합니다.")
    @PatchMapping(value = "/{orderId}/cancel")
    public ResponseDto<Void> updateOrderCancel(
            @Parameter(hidden = true) @AccountID UUID accountId,
            @PathVariable Long orderId
    ) {
        updateUserOrderCancelUseCase.execute(accountId, orderId);
        return ResponseDto.ok(null);
    }
}
