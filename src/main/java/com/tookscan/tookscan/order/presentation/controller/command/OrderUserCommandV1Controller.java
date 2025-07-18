package com.tookscan.tookscan.order.presentation.controller.command;

import com.tookscan.tookscan.core.annotation.security.AccountID;
import com.tookscan.tookscan.core.annotation.swagger.ApiErrorCode;
import com.tookscan.tookscan.core.dto.ResponseDto;
import com.tookscan.tookscan.core.exception.error.ErrorCode;
import com.tookscan.tookscan.order.application.usecase.CreateUserOrderUseCase;
import com.tookscan.tookscan.order.application.usecase.UpdateUserOrderCancelUseCase;
import com.tookscan.tookscan.order.application.usecase.UpdateUserOrderHistoryUseCase;
import com.tookscan.tookscan.order.application.usecase.UpdateUserOrderInfoUseCase;
import com.tookscan.tookscan.order.presentation.dto.request.CreateUserOrderRequestDto;
import com.tookscan.tookscan.order.presentation.dto.request.UpdateUserOrderHistoryRequestDto;
import com.tookscan.tookscan.order.presentation.dto.request.UpdateUserOrderInfoRequestDto;
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
    private final UpdateUserOrderCancelUseCase updateUserOrderCancelUseCase;
    private final UpdateUserOrderInfoUseCase updateUserOrderInfoUseCase;
    private final UpdateUserOrderHistoryUseCase updateUserOrderHistoryUseCase;

    /**
     * 4.1 회원 스캔 주문
     */
    @Operation(summary = "회원 스캔 주문", description = "회원이 주문을 생성합니다.")
    @ApiErrorCode({
        ErrorCode.NOT_FOUND_ACCOUNT,
        ErrorCode.NOT_FOUND_PRICE_POLICY,
        ErrorCode.NOT_FOUND_COUPON,
        ErrorCode.NOT_AVAILABLE_COUPON,
        ErrorCode.USED_COUPON,
        ErrorCode.USER_ONLY_COUPON,
        ErrorCode.INVALID_ARGUMENT,
        ErrorCode.BAD_REQUEST_PARAMETER,
        ErrorCode.ACCESS_DENIED
    })
    @PostMapping()
    public ResponseDto<CreateUserOrderResponseDto> createOrder(
            @Parameter(hidden = true) @AccountID UUID accountId,
            @RequestBody @Valid CreateUserOrderRequestDto requestDto
    ) {
        return ResponseDto.created(createUserOrderUseCase.execute(accountId, requestDto));
    }


    /**
     * 4.3.9 회원 주문 취소하기
     */
    @Operation(summary = "회원 주문 취소하기", description = "회원이 주문을 취소합니다.")
    @ApiErrorCode({
        ErrorCode.NOT_FOUND_ACCOUNT,
        ErrorCode.NOT_FOUND_ORDER,
        ErrorCode.NOT_MATCH_ORDER_USER,
        ErrorCode.NOT_UPDATABLE_ORDER,
        ErrorCode.ACCESS_DENIED
    })
    @PatchMapping(value = "/{orderId}/cancel")
    public ResponseDto<Void> updateOrderCancel(
            @Parameter(hidden = true) @AccountID UUID accountId,
            @PathVariable Long orderId
    ) {
        updateUserOrderCancelUseCase.execute(accountId, orderId);
        return ResponseDto.ok(null);
    }

    /**
     * 4.3.10 회원 주문 정보 수정하기
     */
    @Operation(summary = "회원 주문 정보 수정하기", description = "회원이 주문 정보를 수정합니다.")
    @ApiErrorCode({
        ErrorCode.NOT_FOUND_ACCOUNT,
        ErrorCode.NOT_FOUND_ORDER,
        ErrorCode.NOT_MATCH_ORDER_USER,
        ErrorCode.NOT_UPDATABLE_ORDER,
        ErrorCode.INVALID_ARGUMENT,
        ErrorCode.BAD_REQUEST_PARAMETER,
        ErrorCode.ACCESS_DENIED
    })
    @PatchMapping(value = "/{orderId}/info")
    public ResponseDto<Void> updateOrderInfo(
            @Parameter(hidden = true) @AccountID UUID accountId,
            @PathVariable Long orderId,
            @RequestBody @Valid UpdateUserOrderInfoRequestDto requestDto
    ) {
        updateUserOrderInfoUseCase.execute(accountId, orderId, requestDto);
        return ResponseDto.ok(null);
    }

    /**
     * 4.3.11 회원 주문 내역 수정하기
     */
    @Operation(summary = "회원 주문 내역 수정하기", description = "회원이 주문 내역을 수정합니다.")
    @ApiErrorCode({
        ErrorCode.NOT_FOUND_ACCOUNT,
        ErrorCode.NOT_FOUND_ORDER,
        ErrorCode.NOT_FOUND_PRICE_POLICY,
        ErrorCode.NOT_MATCH_ORDER_USER,
        ErrorCode.NOT_UPDATABLE_ORDER,
        ErrorCode.INVALID_ARGUMENT,
        ErrorCode.BAD_REQUEST_PARAMETER,
        ErrorCode.ACCESS_DENIED
    })
    @PatchMapping(value = "/{orderId}/history")
    public ResponseDto<Void> updateOrderHistory(
            @Parameter(hidden = true) @AccountID UUID accountId,
            @PathVariable Long orderId,
            @RequestBody @Valid UpdateUserOrderHistoryRequestDto requestDto
    ) {
        updateUserOrderHistoryUseCase.execute(accountId, orderId, requestDto);
        return ResponseDto.ok(null);
    }

}
