package com.tookscan.tookscan.core.utility;

import com.tookscan.tookscan.core.exception.error.ErrorCode;
import com.tookscan.tookscan.core.exception.type.CommonException;
import com.tookscan.tookscan.order.domain.Document;
import com.tookscan.tookscan.order.domain.type.EScanStatus;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ScannerUtil {

    @Value("${scanner.base-url}")
    private String SCANNER_BASE_URL;

    @Value("${scanner.is-prod}")
    private Boolean IS_PROD;

    private final RestClientUtil restClientUtil;

    /**
     * 1) 스캐너 작업을 시작하여 taskId 획득
     *
     * @param orderId    주문 ID
     * @param documentId 문서 ID
     * @param filename   스캐너에 전달할 파일 이름 (쿼리 파라미터)
     * @return 스캐너가 반환한 taskId
     */
    public String startScan(Long orderId, Long documentId, String filename) {
        String url = SCANNER_BASE_URL
                + "/api/v1/orders/" + orderId
                + "/documents/" + documentId
                + "/scan?filename=" + filename
                + "&is_prod=" + IS_PROD;

        HttpHeaders headers = new HttpHeaders();

        // 바디 없을 경우 "" 또는 "{}" 등
        String body = "";

        // Map<String, Object>로 응답받기
        Map<String, Object> response = restClientUtil.sendPostMethod(url, headers, body);
        if (Objects.isNull(response)) {
            throw new CommonException(ErrorCode.INTERNAL_SERVER_ERROR, "스캐너 서버 응답이 null입니다.");
        }

        // success
        Boolean success = (Boolean) response.get("success");
        if (!Boolean.TRUE.equals(success)) {
            // error
            String error = (String) response.get("error");
            throw new CommonException(ErrorCode.INTERNAL_SERVER_ERROR,
                    "스캔 요청 실패: " + (error != null ? error : "알 수 없는 에러"));
        }

        // data
        Map<String, Object> data = (Map<String, Object>) response.get("data");
        if (Objects.isNull(data)) {
            throw new CommonException(ErrorCode.INTERNAL_SERVER_ERROR, "스캐너 서버 응답에 data가 없습니다.");
        }

        // task_id
        String taskId = (String) data.get("task_id");
        if (Objects.isNull(taskId)) {
            throw new CommonException(ErrorCode.INTERNAL_SERVER_ERROR, "스캐너 서버 응답에 task_id가 없습니다.");
        }

        return taskId;
    }

    /**
     * 2) 스캐너 서버(Celery 태스크) 상태 조회
     *
     * @param taskId 스캐너 작업 ID
     * @return 작업 상태 (예: PENDING, STARTED, SUCCESS, FAILURE)
     */
    public EScanStatus getScanStatus(String taskId) {
        String url = SCANNER_BASE_URL + "/api/v1/orders/scan/status/" + taskId;

        HttpHeaders headers = new HttpHeaders();

        try {
            Map<String, Object> response = restClientUtil.sendGetMethod(url, headers);

            if (Objects.isNull(response)) {
                throw new CommonException(ErrorCode.INTERNAL_SERVER_ERROR, "스캐너 서버 응답이 null입니다.");
            }

            // data
            Map<String, Object> data = (Map<String, Object>) response.get("data");
            if (Objects.isNull(data)) {
                throw new CommonException(ErrorCode.INTERNAL_SERVER_ERROR, "스캐너 서버 응답에 data가 없습니다.");
            }

            // status
            String status = (String) data.get("status");
            if (Objects.isNull(status)) {
                throw new CommonException(ErrorCode.INTERNAL_SERVER_ERROR, "스캐너 서버 응답에 status가 없습니다.");
            }

            switch (status) {
                case "STARTED":
                    return EScanStatus.IN_PROGRESS;
                case "SUCCESS":
                    return EScanStatus.COMPLETED;
                case "FAILURE":
                    return EScanStatus.FAILED;
                default:
                    return EScanStatus.ENABLE;
            }
        } catch (CommonException e) {
            return EScanStatus.UNABLE;
        }
    }

    public Map<Long, EScanStatus> getScanStatuses(List<Document> documents) {
        return documents.stream()
                .collect(Collectors.toMap(Document::getId, document -> {
                    EScanStatus status = getScanStatus(document.getScanTaskId());
                    if (status == EScanStatus.UNABLE) {
                        return EScanStatus.UNABLE;
                    }
                    if (document.getScanTaskId() == null) {
                        return EScanStatus.ENABLE;
                    }
                    return getScanStatus(document.getScanTaskId());
                }));
    }
}
