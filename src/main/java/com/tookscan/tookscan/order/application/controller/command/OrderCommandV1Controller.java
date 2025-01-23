package com.tookscan.tookscan.order.application.controller.command;

import com.tookscan.tookscan.core.annotation.security.AccountID;
import com.tookscan.tookscan.core.dto.ResponseDto;
import com.tookscan.tookscan.order.application.dto.request.CreateOrderMemoRequestDto;
import com.tookscan.tookscan.order.application.dto.request.CreateUserOrderRequestDto;
import com.tookscan.tookscan.order.application.dto.response.CreateUserOrderResponseDto;
import com.tookscan.tookscan.order.application.usecase.CreateOrderMemoUseCase;
import com.tookscan.tookscan.order.application.usecase.CreateUserOrderUseCase;
import com.tookscan.tookscan.order.application.usecase.UpdateOrderScanUseCase;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/users/orders")
public class OrderCommandV1Controller {
    private final CreateUserOrderUseCase createUserOrderUseCase;
    private final UpdateOrderScanUseCase updateOrderScanUseCase;
    private final CreateOrderMemoUseCase createOrderMemoUseCase;

    /**
     * 4.1 회원 스캔 주문
     */
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
    @PatchMapping(value = "/{orderId}/scan")
    public ResponseDto<Void> updateOrderScan(
            @Parameter(hidden = true) @AccountID UUID accountId,
            @PathVariable Long orderId
    ) {
        updateOrderScanUseCase.execute(accountId, orderId);
        return ResponseDto.ok(null);
    }

    /**
     * 4.5 관리자 주문 메모 작성
     */
    @PostMapping(value = "/admins/orders/{orderId}/memo")
    public ResponseDto<Void> createOrderMemo(
            @Parameter(hidden = true) @AccountID UUID accountId,
            @PathVariable Long orderId,
            @RequestBody @Valid CreateOrderMemoRequestDto requestDto
    ) {
        createOrderMemoUseCase.execute(orderId, requestDto);
        return ResponseDto.created(null);
    }
}
