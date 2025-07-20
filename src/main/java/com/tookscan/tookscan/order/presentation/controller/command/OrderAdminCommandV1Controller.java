package com.tookscan.tookscan.order.presentation.controller.command;

import com.tookscan.tookscan.core.annotation.security.AccountID;
import com.tookscan.tookscan.core.annotation.swagger.ApiErrorCode;
import com.tookscan.tookscan.core.annotation.swagger.ApiErrorExceptions;
import com.tookscan.tookscan.core.dto.ResponseDto;
import com.tookscan.tookscan.core.exception.error.ErrorCode;
import com.tookscan.tookscan.order.application.usecase.CreateAdminOrderCouponUseCase;
import com.tookscan.tookscan.order.application.usecase.CreateAdminOrderMemoUseCase;
import com.tookscan.tookscan.order.application.usecase.DeleteAdminDocumentsUseCase;
import com.tookscan.tookscan.order.application.usecase.DeleteAdminPdfUseCase;
import com.tookscan.tookscan.order.application.usecase.ExportAdminDeliveriesUseCase;
import com.tookscan.tookscan.order.application.usecase.SendAdminPdfUseCase;
import com.tookscan.tookscan.order.application.usecase.UpdateAdminOrderDeliveryTrackingNumberUseCase;
import com.tookscan.tookscan.order.application.usecase.UpdateAdminOrderDeliveryUseCase;
import com.tookscan.tookscan.order.application.usecase.UpdateAdminOrderStatusPaymentWaitingUseCase;
import com.tookscan.tookscan.order.application.usecase.UpdateAdminOrderUseCase;
import com.tookscan.tookscan.order.application.usecase.UpdateAdminOrdersDeliveriesTrackingNumberUseCase;
import com.tookscan.tookscan.order.application.usecase.UpdateAdminOrdersStatusCancelUseCase;
import com.tookscan.tookscan.order.application.usecase.UpdateAdminOrdersStatusCompanyArrivedUseCase;
import com.tookscan.tookscan.order.application.usecase.UpdateAdminOrdersStatusRecoveryOptionUseCase;
import com.tookscan.tookscan.order.application.usecase.UpdateAdminOrdersStatusUseCase;
import com.tookscan.tookscan.order.application.usecase.UploadAdminDocumentsPdfUseCase;
import com.tookscan.tookscan.order.application.usecase.ValidateAdminPdfUseCase;
import com.tookscan.tookscan.order.presentation.dto.request.CreateAdminOrderCouponRequestDto;
import com.tookscan.tookscan.order.presentation.dto.request.CreateAdminOrderMemoRequestDto;
import com.tookscan.tookscan.order.presentation.dto.request.DeleteAdminDocumentsRequestDto;
import com.tookscan.tookscan.order.presentation.dto.request.ExportAdminDeliveriesRequestDto;
import com.tookscan.tookscan.order.presentation.dto.request.UpdateAdminOrderDeliveryRequestDto;
import com.tookscan.tookscan.order.presentation.dto.request.UpdateAdminOrderDeliveryTrackingNumberRequestDto;
import com.tookscan.tookscan.order.presentation.dto.request.UpdateAdminOrderRequestDto;
import com.tookscan.tookscan.order.presentation.dto.request.UpdateAdminOrdersStatusCancelRequestDto;
import com.tookscan.tookscan.order.presentation.dto.request.UpdateAdminOrdersStatusCompanyArrivedRequestDto;
import com.tookscan.tookscan.order.presentation.dto.request.UpdateAdminOrdersStatusRecoveryOptionRequestDto;
import com.tookscan.tookscan.order.presentation.dto.request.UpdateAdminOrdersStatusRequestDto;
import com.tookscan.tookscan.order.presentation.dto.response.ValidateAdminPdfResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "Order", description = "Order 관련 API 입니다.")
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/admins")
public class OrderAdminCommandV1Controller {

    private final CreateAdminOrderMemoUseCase createAdminOrderMemoUseCase;
    private final UpdateAdminOrdersStatusUseCase updateAdminOrdersStatusUseCase;
    private final UpdateAdminOrdersStatusCancelUseCase updateAdminOrdersStatusCancelUseCase;
    private final UpdateAdminOrdersStatusCompanyArrivedUseCase updateAdminOrdersStatusCompanyArrivedUseCase;
    private final UpdateAdminOrderDeliveryUseCase updateAdminOrderDeliveryUseCase;
    private final SendAdminPdfUseCase sendAdminPdfUseCase;
    private final UpdateAdminOrderStatusPaymentWaitingUseCase updateAdminOrderStatusPaymentWaitingUseCase;
    private final UpdateAdminOrdersDeliveriesTrackingNumberUseCase updateAdminOrdersDeliveriesTrackingNumberUseCase;
    private final UpdateAdminOrderDeliveryTrackingNumberUseCase updateAdminOrderDeliveryTrackingNumberUseCase;
    private final DeleteAdminDocumentsUseCase deleteAdminDocumentsUseCase;
    private final UpdateAdminOrderUseCase updateAdminOrderUseCase;
    private final ExportAdminDeliveriesUseCase exportAdminDeliveriesUseCase;
    private final CreateAdminOrderCouponUseCase createAdminOrderCouponUseCase;
    private final UploadAdminDocumentsPdfUseCase uploadAdminDocumentsPdfUseCase;
    private final ValidateAdminPdfUseCase validateAdminPdfUseCase;
    private final UpdateAdminOrdersStatusRecoveryOptionUseCase updateAdminOrdersStatusRecoveryOptionUseCase;
    private final DeleteAdminPdfUseCase deleteAdminPdfUseCase;

    /**
     * 4.1.3 관리자 배송 리스트 내보내기
     */
    @Operation(summary = "관리자 배송 리스트 내보내기", description = "관리자가 배송 리스트를 내보냅니다.")
    @ApiErrorCode({
        ErrorCode.INVALID_ARGUMENT,
        ErrorCode.BAD_REQUEST_PARAMETER,
            ErrorCode.ACCESS_DENIED,
            ErrorCode.NOT_POST_WAITING_ORDER
    })
    @PostMapping(value = "/deliveries/export")
    public ResponseEntity<Resource> exportDeliveries(
            @RequestBody @Valid ExportAdminDeliveriesRequestDto requestDto
    ) {
        // 1) 서비스/UseCase를 호출해 "엑셀 파일(바이트배열)"을 생성
        byte[] excelBytes = exportAdminDeliveriesUseCase.execute(requestDto);

        // 2) 스프링에서 파일 다운로드를 위한 HTTP 응답 헤더 설정
        String fileName = "orders.xlsx";
        ByteArrayResource resource = new ByteArrayResource(excelBytes);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .contentLength(excelBytes.length)
                .body(resource);
    }

    /**
     * 4.1.4 관리자 PDF 워터마크 검수
     */
    @Operation(summary = "관리자 PDF 워터마크 검수", description = "관리자가 PDF 파일에 워터마크를 검수합니다.")
    @PostMapping(value = "/pdfs/validation")
    public ResponseDto<ValidateAdminPdfResponseDto> validatePdf(
            @RequestParam("file") MultipartFile file
    ) {

        return ResponseDto.ok(validateAdminPdfUseCase.execute(file));
    }

    /**
     * 4.1.5 관리자 쿠폰 등록
     */
    @Operation(summary = "관리자 쿠폰 등록", description = "관리자가 쿠폰을 등록합니다.")
    @ApiErrorCode({
        ErrorCode.ALREADY_EXIST_RESOURCE,
        ErrorCode.INVALID_ARGUMENT,
        ErrorCode.BAD_REQUEST_PARAMETER,
        ErrorCode.ACCESS_DENIED
    })
    @PostMapping(value = "/orders/coupons")
    public ResponseDto<CreateAdminOrderCouponRequestDto> createCoupon(
            @RequestBody @Valid CreateAdminOrderCouponRequestDto requestDto
    ) {
        createAdminOrderCouponUseCase.execute(requestDto);
        return ResponseDto.created(null);
    }

    /**
     * 4.1.6 관리자 pdf 파일 업로드
     */
    @Operation(summary = "관리자 pdf 파일 업로드", description = "관리자가 pdf 파일을 업로드합니다.")
    @ApiErrorCode({
        ErrorCode.NOT_FOUND_DOCUMENT,
        ErrorCode.UNSUPPORTED_MEDIA_TYPE,
        ErrorCode.UPLOAD_FILE_ERROR,
        ErrorCode.ACCESS_DENIED
    })
    @PostMapping(value = "/documents/{documentId}/pdfs", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseDto<Void> uploadPdf(
            @PathVariable Long documentId,
            @RequestPart("file") MultipartFile file
    ) {
        uploadAdminDocumentsPdfUseCase.execute(documentId, file);
        return ResponseDto.ok(null);
    }
    /**
     * 4.3.2 관리자 주문 상태 일괄 변경
     */
    @Operation(summary = "관리자 주문 상태 일괄 변경", description = "관리자가 여러 주문의 상태를 일괄 변경합니다.")
    @ApiErrorCode({
            ErrorCode.NOT_FOUND_ORDER,
            ErrorCode.INVALID_ORDER_STATUS,
            ErrorCode.ACCESS_DENIED
    })
    @ApiErrorExceptions({
            MethodArgumentNotValidException.class
    })
    @PostMapping(value = "/orders/status")
    public ResponseDto<Void> updateOrderStatus(
         @RequestBody @Valid UpdateAdminOrdersStatusRequestDto requestDto
    ) {
        updateAdminOrdersStatusUseCase.execute(requestDto);
        return ResponseDto.ok(null);
    }

    /**
     * 4.3.3 관리자 주문 메모 작성
     */
    @Operation(summary = "관리자 주문 메모 작성", description = "관리자가 주문에 메모를 작성합니다.")
    @ApiErrorCode({
            ErrorCode.NOT_FOUND_ORDER,
            ErrorCode.ACCESS_DENIED
    })
    @ApiErrorExceptions({
            MethodArgumentNotValidException.class
    })
    @PostMapping(value = "/orders/{orderId}/memo")
    public ResponseDto<Void> createOrderMemo(
            @PathVariable Long orderId,
            @RequestBody @Valid CreateAdminOrderMemoRequestDto requestDto
    ) {
        createAdminOrderMemoUseCase.execute(orderId, requestDto);
        return ResponseDto.created(null);
    }

    /**
     * 4.3.4 관리자 배송지 정보 수정
     */
    @Operation(summary = "관리자 배송지 정보 수정", description = "관리자가 주문의 배송지 정보를 수정합니다.")
    @ApiErrorCode({
        ErrorCode.NOT_FOUND_DELIVERY,
        ErrorCode.INVALID_ARGUMENT,
        ErrorCode.BAD_REQUEST_PARAMETER,
        ErrorCode.ACCESS_DENIED
    })
    @PutMapping(value = "/deliveries/{deliveryId}")
    public ResponseDto<Void> updateOrderAddress(
            @PathVariable Long deliveryId,
            @RequestBody @Valid UpdateAdminOrderDeliveryRequestDto requestDto
    ) {
        updateAdminOrderDeliveryUseCase.execute(deliveryId, requestDto);
        return ResponseDto.ok(null);
    }

    /**
     * 4.3.5 관리자 파일 전송
     */
    @Operation(summary = "관리자 파일 전송", description = "관리자가 주문의 파일을 전송합니다.")
    @ApiErrorCode({
        ErrorCode.NOT_FOUND_ORDER,
        ErrorCode.INVALID_ORDER_STATUS,
        ErrorCode.ACCESS_DENIED
    })
    @PostMapping(value = "/orders/{id}/send-pdfs")
    public ResponseDto<Void> sendPdf(
            @PathVariable Long id
    ) {
        sendAdminPdfUseCase.execute(id);
        return ResponseDto.ok(null);
    }

    /**
     * 4.3.7 관리자 결제 요청
     */
    @Operation(summary = "관리자 결제 요청", description = "관리자가 주문에 대해 결제를 요청합니다.")
    @ApiErrorCode({
        ErrorCode.NOT_FOUND_ORDER,
        ErrorCode.INVALID_ORDER_STATUS,
        ErrorCode.ACCESS_DENIED
    })
    @PatchMapping(value = "/orders/{id}/payment-requests")
    public ResponseDto<Void> requestPayment(
            @PathVariable Long id
    ) {
        updateAdminOrderStatusPaymentWaitingUseCase.execute(id);
        return ResponseDto.ok(null);
    }

    /**
     * 4.3.7 관리자 운송장 번호 등록
     */
    @Operation(summary = "관리자 운송장 번호 등록", description = "관리자가 주문에 운송장 번호를 등록합니다.")
    @ApiErrorCode({
        ErrorCode.NOT_FOUND_DELIVERY,
        ErrorCode.INVALID_ARGUMENT,
        ErrorCode.BAD_REQUEST_PARAMETER,
        ErrorCode.ACCESS_DENIED
    })
    @PatchMapping(value = "/deliveries/{deliveryId}/tracking-number")
    public ResponseDto<Void> updateOrderTrackingNumber(
            @PathVariable Long deliveryId,
            @RequestBody @Valid UpdateAdminOrderDeliveryTrackingNumberRequestDto requestDto
    ) {
        updateAdminOrderDeliveryTrackingNumberUseCase.execute(deliveryId, requestDto);
        return ResponseDto.ok(null);
    }

    /**
     * 4.3.8 관리자 운송장 번호 일괄 등록
     */
    @Operation(summary = "관리자 운송장 번호 일괄 등록", description = "관리자가 여러 주문의 운송장 번호를 일괄 등록합니다.")
    @ApiErrorCode({
        ErrorCode.NOT_FOUND_DELIVERY,
        ErrorCode.UNSUPPORTED_MEDIA_TYPE,
        ErrorCode.INVALID_ARGUMENT,
        ErrorCode.BAD_REQUEST_PARAMETER,
        ErrorCode.ACCESS_DENIED
    })
    @PostMapping(value = "/deliveries/tracking-number", consumes = "multipart/form-data")
    public ResponseDto<Void> updateOrderTrackingNumber(
            @RequestParam("file") MultipartFile file
    ) {
        updateAdminOrdersDeliveriesTrackingNumberUseCase.execute(file);
        return ResponseDto.ok(null);
    }

    /**
     * 4.4.1 관리자 주문 상세 상품 수정
     */
    @Operation(summary = "관리자 주문 상세 상품 수정", description = "관리자가 주문의 상세 상품을 수정합니다.")
    @ApiErrorCode({
        ErrorCode.NOT_FOUND_ORDER,
        ErrorCode.NOT_FOUND_PRICE_POLICY,
        ErrorCode.INVALID_ARGUMENT,
        ErrorCode.BAD_REQUEST_PARAMETER,
        ErrorCode.ACCESS_DENIED
    })
    @PutMapping(value = "orders/{orderId}")
    public ResponseDto<Void> updateOrderDocuments(
            @PathVariable Long orderId,
            @RequestBody @Valid UpdateAdminOrderRequestDto requestDto
    ) {
        updateAdminOrderUseCase.execute(orderId, requestDto);
        return ResponseDto.ok(null);
    }

    /**
     * 4.5.1 관리자 주문 일괄 취소
     */
    @Operation(summary = "관리자 주문 일괄 취소", description = "관리자가 여러 주문을 취소합니다.")
    @ApiErrorCode({
        ErrorCode.NOT_FOUND_ORDER,
        ErrorCode.INVALID_ARGUMENT,
        ErrorCode.BAD_REQUEST_PARAMETER,
        ErrorCode.ACCESS_DENIED
    })
    @PatchMapping(value = "/orders/cancel")
    public ResponseDto<Void> deleteOrders(
            @RequestBody @Valid UpdateAdminOrdersStatusCancelRequestDto requestDto
    ) {
        updateAdminOrdersStatusCancelUseCase.execute(requestDto);
        return ResponseDto.ok(null);
    }

    /**
     * 4.5.2 관리자 상품 일괄 삭제
     */
    @Operation(summary = "관리자 상품 일괄 삭제", description = "관리자가 여러 상품을 삭제합니다.")
    @ApiErrorCode({
        ErrorCode.NOT_FOUND_DOCUMENT,
        ErrorCode.INVALID_ARGUMENT,
        ErrorCode.BAD_REQUEST_PARAMETER,
        ErrorCode.ACCESS_DENIED
    })
    @DeleteMapping(value = "/documents")
    public ResponseDto<Void> deleteDocuments(
            @RequestBody @Valid DeleteAdminDocumentsRequestDto requestDto
    ) {
        deleteAdminDocumentsUseCase.execute(requestDto);
        return ResponseDto.ok(null);
    }

    /**
     * 관리자 주문 일괄 복원 완료
     */
    @Operation(summary = "관리자 주문 일괄 복원 완료", description = "관리자가 여러 주문의 복원 작업을 완료합니다.")
    @ApiErrorCode({
        ErrorCode.NOT_FOUND_ACCOUNT,
        ErrorCode.NOT_FOUND_ORDER,
        ErrorCode.NOT_RECOVERY_IN_PROGRESS,
        ErrorCode.INVALID_ARGUMENT,
        ErrorCode.BAD_REQUEST_PARAMETER,
        ErrorCode.ACCESS_DENIED
    })
    @PatchMapping(value = "/orders/recovery-completed")
    public ResponseDto<Void> updateOrdersRecoveryCompleted(
            @Parameter(hidden = true) @AccountID UUID accountId,
            @RequestBody @Valid UpdateAdminOrdersStatusRecoveryOptionRequestDto requestDto
    ) {
        updateAdminOrdersStatusRecoveryOptionUseCase.execute(accountId, requestDto);
        return ResponseDto.ok(null);
    }

    /**
     * 관리자 주문 일괄 업체 도착 상태 변경
     */
    @Operation(summary = "관리자 주문 일괄 업체 도착 상태 변경", description = "관리자가 여러 주문의 상태를 업체 도착으로 일괄 변경합니다.")
    @ApiErrorCode({
        ErrorCode.NOT_FOUND_ORDER,
        ErrorCode.NOT_PAYMENT_COMPLETED,
        ErrorCode.INVALID_ARGUMENT,
        ErrorCode.BAD_REQUEST_PARAMETER,
        ErrorCode.ACCESS_DENIED
    })
    @PatchMapping(value = "/orders/company-arrived")
    public ResponseDto<Void> updateOrdersStatusCompanyArrived(
            @RequestBody @Valid UpdateAdminOrdersStatusCompanyArrivedRequestDto requestDto
    ) {
        updateAdminOrdersStatusCompanyArrivedUseCase.execute(requestDto);
        return ResponseDto.ok(null);
    }

    /**
     * 관리자 PDF 삭제
     */
    @Operation(summary = "관리자 PDF 삭제", description = "관리자가 상품의 PDF를 삭제합니다.")
    @ApiErrorCode({
            ErrorCode.NOT_FOUND_PDF_FILE,
            ErrorCode.INVALID_ARGUMENT,
            ErrorCode.BAD_REQUEST_PARAMETER,
            ErrorCode.ACCESS_DENIED
    })
    @DeleteMapping(value = "/orders/documents/pdf/{pdfId}")
    public ResponseDto<Void> deleteOrdersDocumentsPdf(
            @PathVariable Long pdfId
    ) {
        deleteAdminPdfUseCase.execute(pdfId);
        return ResponseDto.ok(null);
    }
}
