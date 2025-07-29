package com.tookscan.tookscan.order.presentation.controller.query;

import com.tookscan.tookscan.core.annotation.swagger.ApiErrorCode;
import com.tookscan.tookscan.core.dto.ResponseDto;
import com.tookscan.tookscan.core.exception.error.ErrorCode;
import com.tookscan.tookscan.order.application.usecase.ReadAdminDocumentsPdfsUseCase;
import com.tookscan.tookscan.order.application.usecase.ReadAdminOrderBriefsUseCase;
import com.tookscan.tookscan.order.application.usecase.ReadAdminOrderDetailUseCase;
import com.tookscan.tookscan.order.application.usecase.ReadAdminOrderOverviewsUseCase;
import com.tookscan.tookscan.order.application.usecase.ReadStatisticsSummariesUseCase;
import com.tookscan.tookscan.order.domain.type.EOrderStatus;
import com.tookscan.tookscan.order.presentation.dto.response.ReadAdminDocumentsPdfsResponseDto;
import com.tookscan.tookscan.order.presentation.dto.response.ReadAdminOrderBriefsResponseDto;
import com.tookscan.tookscan.order.presentation.dto.response.ReadAdminOrderDetailResponseDto;
import com.tookscan.tookscan.order.presentation.dto.response.ReadAdminOrderOverviewsResponseDto;
import com.tookscan.tookscan.order.presentation.dto.response.ReadStatisticsSummariesResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Order", description = "Order 관련 API 입니다.")
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/admins")
public class OrderAdminQueryV1Controller {

    private final ReadAdminOrderBriefsUseCase readAdminOrderBriefsUseCase;
    private final ReadAdminOrderDetailUseCase readAdminOrderDetailUseCase;
    private final ReadAdminOrderOverviewsUseCase readAdminOrderOverviewsUseCase;
    private final ReadStatisticsSummariesUseCase readStatisticsSummariesUseCase;
    private final ReadAdminDocumentsPdfsUseCase readAdminDocumentsPdfsUseCase;

    /**
     * 4.2.5 관리자 주문 요약 정보 조회
     */
    @Operation(summary = "관리자 주문 요약 정보 조회", description = "관리자가 주문 요약 정보를 조회합니다.")
    @ApiErrorCode({
        ErrorCode.ACCESS_DENIED
    })
    @GetMapping("/orders/briefs")
    public ResponseDto<ReadAdminOrderBriefsResponseDto> readOrderBriefs() {
        return ResponseDto.ok(readAdminOrderBriefsUseCase.execute());
    }

    /**
     * 4.2.7 관리자 스캔 파일 다운로드
     */
    @Operation(summary = "관리자 스캔 PDF 파일 다운로드", description = "관리자가 주문의 스캔 PDF 파일을 다운로드합니다.")
    @ApiErrorCode({
        ErrorCode.NOT_FOUND_DOCUMENT,
        ErrorCode.ACCESS_DENIED
    })
    @GetMapping("/documents/{documentId}/pdfs")
    public ResponseDto<ReadAdminDocumentsPdfsResponseDto> downloadScanFile(
            @PathVariable Long documentId
    ) {
        return ResponseDto.ok(readAdminDocumentsPdfsUseCase.execute(documentId));
    }

    /**
     * 4.2.8 관리자 주문 상세 조회
     */
    @Operation(summary = "관리자 주문 상세 조회", description = "관리자가 주문 상세 내역을 조회합니다.")
    @ApiErrorCode({
        ErrorCode.NOT_FOUND_ORDER,
        ErrorCode.ACCESS_DENIED
    })
    @GetMapping("/orders/{orderId}/details")
    public ResponseDto<ReadAdminOrderDetailResponseDto> readOrderDocumentsOverviews(
            @PathVariable Long orderId
    ) {
        return ResponseDto.ok(readAdminOrderDetailUseCase.execute(orderId));
    }


    /**
     * 4.2.12 관리자 주문 리스트 조회
     */
    @Operation(summary = "관리자 주문 리스트 조회", description = "관리자가 주문 리스트를 조회합니다.")
    @ApiErrorCode({
        ErrorCode.INVALID_ARGUMENT,
        ErrorCode.BAD_REQUEST_PARAMETER,
        ErrorCode.ACCESS_DENIED
    })
    @GetMapping("/orders/overviews")
    public ResponseDto<ReadAdminOrderOverviewsResponseDto> readOrderOverviews(
            @RequestParam(value = "page", defaultValue = "1") @Min(value = 1, message = "페이지는 1 이상이어야 합니다") Integer page,
            @RequestParam(value = "size", defaultValue = "10") @Min(value = 1, message = "페이지 크기는 1 이상이어야 합니다") Integer size,
            @RequestParam(value = "start-date", required = false) String startDate,
            @RequestParam(value = "end-date", required = false) String endDate,
            @RequestParam(value = "search", required = false) String search,
            @Parameter(description = "검색 타입 (order-number, name, document-name, tracking-number, email, phone-number, address, memo)") @RequestParam(value = "search-type", required = false) String searchType,
            @Parameter(description = "정렬 기준 (order-date, payment-date, total-amount, payment-amount, document-count, pdf-send-date)") @RequestParam(value = "sort", defaultValue = "order-date") String sort,
            @RequestParam(value = "direction", defaultValue = "ASC") Direction direction,
            @RequestParam(value = "order-status", required = false) EOrderStatus orderStatus,
            @RequestParam(value = "is-one-day-scan", required = false) Boolean isOneDayScan,
            @RequestParam(value = "has-recovery-option", required = false) Boolean hasRecoveryOption,
            @RequestParam(value = "is-as-in-progress", required = false) Boolean isAsInProgress,
            @RequestParam(value = "is-in-progress", required = false) Boolean isInProgress
    ) {
        return ResponseDto.ok(
                readAdminOrderOverviewsUseCase.execute(page, size, startDate, endDate, search, 
                        searchType, sort, direction, orderStatus, isOneDayScan, hasRecoveryOption, isAsInProgress, isInProgress));
    }


    /**
     * 5.2.1 관리자 통계 조회
     */
    @Operation(summary = "관리자 통계 조회", description = "관리자가 통계 정보를 조회합니다.")
    @ApiErrorCode({
        ErrorCode.INVALID_ARGUMENT,
        ErrorCode.BAD_REQUEST_PARAMETER,
        ErrorCode.ACCESS_DENIED
    })
    @GetMapping("/statistics/summaries")
    public ResponseDto<ReadStatisticsSummariesResponseDto> readStatisticsSummaries(
            @RequestParam(value = "start-year-month") String startYearMonth,
            @RequestParam(value = "end-year-month") String endYearMonth,
            @RequestParam(value = "is-applied", required = false) Boolean isApplied,
            @RequestParam(value = "is_arrived", required = false) Boolean isArrived,
            @RequestParam(value = "is_completed", required = false) Boolean isCompleted
    ) {
        return ResponseDto.ok(readStatisticsSummariesUseCase.execute(startYearMonth, endYearMonth,
                isApplied, isArrived, isCompleted));
    }

}
