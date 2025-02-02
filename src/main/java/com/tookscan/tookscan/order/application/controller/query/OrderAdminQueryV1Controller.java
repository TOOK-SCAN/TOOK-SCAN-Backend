package com.tookscan.tookscan.order.application.controller.query;

import com.tookscan.tookscan.core.dto.ResponseDto;
import com.tookscan.tookscan.order.application.dto.response.ReadAdminDeliveriesOverviewsResponseDto;
import com.tookscan.tookscan.order.application.dto.response.ReadAdminDeliveriesSummariesResponseDto;
import com.tookscan.tookscan.order.application.dto.response.ReadAdminOrderBriefResponseDto;
import com.tookscan.tookscan.order.application.dto.response.ReadAdminOrderBriefsResponseDto;
import com.tookscan.tookscan.order.application.dto.response.ReadAdminOrderDeliveryOverviewResponseDto;
import com.tookscan.tookscan.order.application.dto.response.ReadAdminOrderDocumentsOverviewsResponseDto;
import com.tookscan.tookscan.order.application.dto.response.ReadAdminOrderOverviewsResponseDto;
import com.tookscan.tookscan.order.application.dto.response.ReadAdminOrderSummariesResponseDto;
import com.tookscan.tookscan.order.application.dto.response.ReadStatisticsSummariesResponseDto;
import com.tookscan.tookscan.order.application.usecase.ReadAdminDeliveriesOverviewsUseCase;
import com.tookscan.tookscan.order.application.usecase.ReadAdminDeliveriesSummariesUseCase;
import com.tookscan.tookscan.order.application.usecase.ReadAdminOrderBriefUseCase;
import com.tookscan.tookscan.order.application.usecase.ReadAdminOrderBriefsUseCase;
import com.tookscan.tookscan.order.application.usecase.ReadAdminOrderDeliveryOverviewUseCase;
import com.tookscan.tookscan.order.application.usecase.ReadAdminOrderDocumentsOverviewsUseCase;
import com.tookscan.tookscan.order.application.usecase.ReadAdminOrderOverviewsUseCase;
import com.tookscan.tookscan.order.application.usecase.ReadAdminOrderSummariesUseCase;
import com.tookscan.tookscan.order.application.usecase.ReadStatisticsSummariesUseCase;
import com.tookscan.tookscan.order.domain.type.EOrderStatus;
import io.swagger.v3.oas.annotations.Operation;
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
    private final ReadAdminOrderSummariesUseCase readAdminOrderSummariesUseCase;
    private final ReadAdminOrderDocumentsOverviewsUseCase readAdminOrderDocumentsOverviewsUseCase;
    private final ReadAdminOrderBriefUseCase readAdminOrderBriefUseCase;
    private final ReadAdminOrderDeliveryOverviewUseCase readAdminOrderDeliveryOverviewUseCase;
    private final ReadAdminOrderOverviewsUseCase readAdminOrderOverviewsUseCase;
    private final ReadAdminDeliveriesSummariesUseCase readAdminDeliveriesSummariesUseCase;
    private final ReadAdminDeliveriesOverviewsUseCase readAdminDeliveriesOverviewsUseCase;
    private final ReadStatisticsSummariesUseCase readStatisticsSummariesUseCase;

    /**
     * 4.2.5 관리자 주문 요약 정보 조회
     */
    @Operation(summary = "관리자 주문 요약 정보 조회", description = "관리자가 주문 요약 정보를 조회합니다.")
    @GetMapping("/orders/briefs")
    public ResponseDto<ReadAdminOrderBriefsResponseDto> readOrderBriefs() {
        return ResponseDto.ok(readAdminOrderBriefsUseCase.execute());
    }

    /**
     * 4.2.6 관리자 주문 리스트 요약 정보 조회
     */
    @Operation(summary = "관리자 주문 리스트 요약 정보 조회", description = "관리자가 주문 리스트 요약 정보를 조회합니다.")
    @GetMapping("/orders/summaries")
    public ResponseDto<ReadAdminOrderSummariesResponseDto> readOrderSummaries(
            @RequestParam(value = "page", defaultValue = "1") @Min(value = 1, message = "페이지는 1 이상이어야 합니다") Integer page,
            @RequestParam(value = "size", defaultValue = "10") @Min(value = 1, message = "페이지 크기는 1 이상이어야 합니다") Integer size,
            @RequestParam(value = "start-date", required = false) String startDate,
            @RequestParam(value = "end-date", required = false) String endDate,
            @RequestParam(value = "search", required = false) String search,
            @RequestParam(value = "search-type", required = false) String searchType,
            @RequestParam(value = "sort", defaultValue = "order-date") String sort,
            @RequestParam(value = "direction", defaultValue = "ASC") Direction direction
    ) {
        return ResponseDto.ok(readAdminOrderSummariesUseCase.execute(page, size, startDate, endDate, search, searchType, sort, direction));
    }

    /**
     * 4.2.8 관리자 주문 상세 상품 조회
     */
    @Operation(summary = "관리자 주문 상세 상품 내역 조회", description = "관리자가 주문 상세 상품 내역을 조회합니다.")
    @GetMapping("/orders/{orderId}/documents/overviews")
    public ResponseDto<ReadAdminOrderDocumentsOverviewsResponseDto> readOrderDocumentsOverviews(
            @PathVariable Long orderId
    ) {
        return ResponseDto.ok(readAdminOrderDocumentsOverviewsUseCase.execute(orderId));
    }
  
    /**
     * 4.2.10 관리자 주문 상세 배송 조회
     */
    @Operation(summary = "관리자 주문 상세 배송 조회", description = "관리자가 주문 상세 배송 정보를 조회합니다.")
    @GetMapping("/orders/{orderId}/delivery/overview")
    public ResponseDto<ReadAdminOrderDeliveryOverviewResponseDto> readOrderDeliveriesOverviews(
            @PathVariable Long orderId
    ) {
        return ResponseDto.ok(readAdminOrderDeliveryOverviewUseCase.execute(orderId));
    }

    /**
     * 4.2.11 관리자 주문 상세 간단 조회
     */
    @Operation(summary = "관리자 주문 상세 간단 조회", description = "관리자가 주문 상세 간단 정보를 조회합니다.")
    @GetMapping("/orders/{orderId}/brief")
    public ResponseDto<ReadAdminOrderBriefResponseDto> readOrderBrief(
            @PathVariable Long orderId
    ) {
        return ResponseDto.ok(readAdminOrderBriefUseCase.execute(orderId));
    }
  
    /**
     * 4.2.12 관리자 주문 리스트 조회
     */
    @Operation(summary = "관리자 주문 리스트 조회", description = "관리자가 주문 리스트를 조회합니다.")
    @GetMapping("/orders/overviews")
    public ResponseDto<ReadAdminOrderOverviewsResponseDto> readOrderOverviews(
            @RequestParam(value = "page", defaultValue = "1") @Min(value = 1, message = "페이지는 1 이상이어야 합니다") Integer page,
            @RequestParam(value = "size", defaultValue = "10") @Min(value = 1, message = "페이지 크기는 1 이상이어야 합니다") Integer size,
            @RequestParam(value = "start-date", required = false) String startDate,
            @RequestParam(value = "end-date", required = false) String endDate,
            @RequestParam(value = "search", required = false) String search,
            @RequestParam(value = "search-type", required = false) String searchType,
            @RequestParam(value = "sort", defaultValue = "order-date") String sort,
            @RequestParam(value = "direction", defaultValue = "ASC") Direction direction,
            @RequestParam(value = "order-status", required = false) EOrderStatus orderStatus
    ) {
        return ResponseDto.ok(
                readAdminOrderOverviewsUseCase.execute(page, size, startDate, endDate, search, searchType, sort,
                        direction, orderStatus));
    }

    /**
     * 4.2.13 관리자 배송 리스트 요약 조회
     */
    @Operation(summary = "관리자 배송 리스트 요약 조회", description = "관리자가 배송 리스트 요약 정보를 조회합니다.")
    @GetMapping("/deliveries/summaries")
    public ResponseDto<ReadAdminDeliveriesSummariesResponseDto> readDeliveriesSummaries(
            @RequestParam(value = "page", defaultValue = "1") @Min(value = 1, message = "페이지는 1 이상이어야 합니다") Integer page,
            @RequestParam(value = "size", defaultValue = "10") @Min(value = 1, message = "페이지 크기는 1 이상이어야 합니다") Integer size,
            @RequestParam(value = "start-date", required = false) String startDate,
            @RequestParam(value = "end-date", required = false) String endDate,
            @RequestParam(value = "search", required = false) String search,
            @RequestParam(value = "search-type", required = false) String searchType
    ) {
        return ResponseDto.ok(
                readAdminDeliveriesSummariesUseCase.execute(page, size, startDate, endDate, search, searchType));
    }

    /**
     * 4.2.14 관리자 배송 리스트 조회
     */
    @Operation(summary = "관리자 배송 리스트 조회", description = "관리자가 배송 리스트를 조회합니다.")
    @GetMapping("/deliveries/overviews")
    public ResponseDto<ReadAdminDeliveriesOverviewsResponseDto> readDeliveriesOverviews(
            @RequestParam(value = "page", defaultValue = "1") @Min(value = 1, message = "페이지는 1 이상이어야 합니다") Integer page,
            @RequestParam(value = "size", defaultValue = "10") @Min(value = 1, message = "페이지 크기는 1 이상이어야 합니다") Integer size,
            @RequestParam(value = "start-date", required = false) String startDate,
            @RequestParam(value = "end-date", required = false) String endDate,
            @RequestParam(value = "search", required = false) String search,
            @RequestParam(value = "search-type", required = false) String searchType
    ) {
        return ResponseDto.ok(
                readAdminDeliveriesOverviewsUseCase.execute(page, size, startDate, endDate, search, searchType));
    }

    /**
     * 5.2.1 관리자 통계 조회
     */
    @Operation(summary = "관리자 통계 조회", description = "관리자가 통계 정보를 조회합니다.")
    @GetMapping("/statistics/summaries")
    public ResponseDto<ReadStatisticsSummariesResponseDto> readStatisticsSummaries(
            @RequestParam(value = "start-year-month", required = false) String startYearMonth,
            @RequestParam(value = "end-year-month", required = false) String endYearMonth
    ) {
        return ResponseDto.ok(readStatisticsSummariesUseCase.execute(startYearMonth, endYearMonth));
    }
}
