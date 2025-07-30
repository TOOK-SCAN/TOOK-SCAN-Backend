package com.tookscan.tookscan.core.utility;

import static org.springframework.http.MediaType.APPLICATION_JSON;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DeliveryTrackerUtil {

    private final RestClientUtil restClientUtil;

    @Value("${delivery-tracker.url}")
    private String API_URL;

    @Value("${delivery-tracker.client-id}")
    private String CLIENT_ID;

    @Value("${delivery-tracker.client-secret}")
    private String CLIENT_SECRET;

    @Value("${delivery-tracker.carrier-id}")
    private String CARRIER_ID;

    @Value("${delivery-tracker.carrier-name}")
    private String CARRIER_NAME;

    // GraphQL 쿼리 (after 파라미터를 통한 페이지네이션 지원)
    private static final String TRACK_DELIVERY_QUERY =
            "query Track($carrierId: ID!, $trackingNumber: String!, $after: String) {"
                    + "  track(carrierId: $carrierId, trackingNumber: $trackingNumber) {"
                    + "    lastEvent {"
                    + "      time"
                    + "      status { code name }"
                    + "      description"
                    + "    }"
                    + "    events(first: 10, after: $after) {"
                    + "      pageInfo { hasNextPage endCursor }"
                    + "      edges {"
                    + "        node {"
                    + "          time"
                    + "          status { code name }"
                    + "          description"
                    + "        }"
                    + "      }"
                    + "    }"
                    + "  }"
                    + "}";

    public String getCarrierName() {
        return CARRIER_NAME;
    }

    /**
     * carrierId와 trackingNumber를 이용해 배송 조회를 수행합니다. events 필드에 대해 커서 페이지네이션을 적용하여 전체 이벤트를 수집한 후, time 필드를 기준으로 오름차순 정렬하여
     * 반환합니다.
     */
    public List<Map<String, Object>> trackDelivery(String trackingNumber) {
        List<Map<String, Object>> allEvents = new ArrayList<>();
        String after = null;
        boolean hasNextPage = true;

        while (hasNextPage) {
            // 요청 본문 구성 (LinkedHashMap 사용)
            Map<String, Object> requestBody = new LinkedHashMap<>();
            requestBody.put("query", TRACK_DELIVERY_QUERY);
            Map<String, Object> variables = new LinkedHashMap<>();
            variables.put("carrierId", CARRIER_ID);
            variables.put("trackingNumber", trackingNumber);
            // after 값이 null인 경우에도 "after": null 형태로 보내기 위해 넣음
            variables.put("after", after);
            requestBody.put("variables", variables);

            // 올바른 JSON 문자열로 직렬화 (toString() 대신)
            String jsonBody = new JSONObject(requestBody).toJSONString();

            // 헤더 구성
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(APPLICATION_JSON);
            headers.set("Authorization", "TRACKQL-API-KEY " + CLIENT_ID + ":" + CLIENT_SECRET);

            Map<String, Object> response = null;
            try {
                response = restClientUtil.sendPost(API_URL, headers, jsonBody);
            } catch (Exception e) {
                StructuredLoggerUtil.error(log)
                        .message("Delivery tracking request failed")
                        .details(Map.of(
                                "tracking_number", trackingNumber
                        ))
                        .exception(e)
                        .log();
                break;
            }

            if (response == null) {
                StructuredLoggerUtil.error(log)
                        .message("Delivery tracking response is null")
                        .details(Map.of(
                                "tracking_number", trackingNumber
                        ))
                        .log();
                break;
            }

            // 응답 파싱
            Map<String, Object> data = (Map<String, Object>) response.get("data");
            if (data == null) {
                StructuredLoggerUtil.error(log)
                        .message("Delivery tracking response data field is null")
                        .details(Map.of(
                                "tracking_number", trackingNumber
                        ))
                        .log();
                break;
            }
            Map<String, Object> track = (Map<String, Object>) data.get("track");
            if (track == null) {
                StructuredLoggerUtil.error(log)
                        .message("Delivery tracking response track field is null")
                        .details(Map.of(
                                "tracking_number", trackingNumber
                        ))
                        .log();
                break;
            }
            Map<String, Object> eventsObj = (Map<String, Object>) track.get("events");
            if (eventsObj != null) {
                List<?> edges = (List<?>) eventsObj.get("edges");
                if (edges != null) {
                    for (Object edgeObj : edges) {
                        Map<String, Object> edge = (Map<String, Object>) edgeObj;
                        Map<String, Object> node = (Map<String, Object>) edge.get("node");
                        if (node != null) {
                            allEvents.add(node);
                        }
                    }
                }
                Map<String, Object> pageInfo = (Map<String, Object>) eventsObj.get("pageInfo");
                if (pageInfo != null) {
                    hasNextPage = Boolean.TRUE.equals(pageInfo.get("hasNextPage"));
                    after = (String) pageInfo.get("endCursor");
                } else {
                    hasNextPage = false;
                }
            } else {
                hasNextPage = false;
            }
        }

        // 수집한 이벤트 리스트를 time 필드를 기준으로 오름차순 정렬
        allEvents.sort((event1, event2) -> {
            String time1 = (String) event1.get("time");
            String time2 = (String) event2.get("time");
            return OffsetDateTime.parse(time1).compareTo(OffsetDateTime.parse(time2));
        });

        return allEvents;
    }
}