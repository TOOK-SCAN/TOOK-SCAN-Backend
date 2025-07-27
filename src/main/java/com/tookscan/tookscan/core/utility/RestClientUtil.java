package com.tookscan.tookscan.core.utility;

import static org.springframework.http.MediaType.APPLICATION_JSON;

import com.tookscan.tookscan.core.exception.error.ErrorCode;
import com.tookscan.tookscan.core.exception.type.CommonException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import net.minidev.json.parser.ParseException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;

@Component
@Slf4j
@RequiredArgsConstructor
public class RestClientUtil {

    private final RestClient restClient;

    public Map<String, Object> sendGetMethod(String url, HttpHeaders headers) {
        try {
            return Objects.requireNonNull(
                    restClient.get()
                            .uri(url)
                            .headers(httpHeaders -> httpHeaders.addAll(headers))
                            .retrieve()
                            .onStatus(HttpStatusCode::is4xxClientError, (request, response) -> {
                                throw new CommonException(ErrorCode.REST_CLIENT_ERROR);
                            })
                            .onStatus(HttpStatusCode::is5xxServerError, (request, response) -> {
                                throw new CommonException(ErrorCode.REST_CLIENT_ERROR);
                            })
                            // toEntity(...) 말고 toEntity(Map.class)도 가능
                            .toEntity(Map.class)
                            .getBody()
            );
        } catch (ResourceAccessException e) {
            // 타임아웃의 경우 ResourceAccessException의 원인이 SocketTimeoutException임
            if (e.getCause() instanceof SocketTimeoutException) {
                throw new CommonException(ErrorCode.EXTERNAL_SERVER_TIMEOUT);
            }
            throw new CommonException(ErrorCode.REST_CLIENT_ERROR);
        } catch (Exception e) {
            throw new CommonException(ErrorCode.REST_CLIENT_ERROR);
        }
    }

    public JSONObject sendGet(String url, HttpHeaders headers) {
        try {
            return new JSONObject(Objects.requireNonNull(
                    restClient.get()
                            .uri(url)
                            .headers(httpHeaders -> httpHeaders.addAll(headers))
                            .retrieve()
                            .onStatus(HttpStatusCode::is4xxClientError, (request, response) -> {
                                throw new CommonException(ErrorCode.REST_CLIENT_ERROR);
                            })
                            .onStatus(HttpStatusCode::is5xxServerError, (request, response) -> {
                                throw new CommonException(ErrorCode.REST_CLIENT_ERROR);
                            })
                            .toEntity(JSONObject.class)
                            .getBody()));
        } catch (ResourceAccessException e) {
            // 타임아웃의 경우 ResourceAccessException의 원인이 SocketTimeoutException임
            if (e.getCause() instanceof SocketTimeoutException) {
                throw new CommonException(ErrorCode.EXTERNAL_SERVER_TIMEOUT);
            }
            throw new CommonException(ErrorCode.REST_CLIENT_ERROR);
        } catch (Exception e) {
            throw new CommonException(ErrorCode.REST_CLIENT_ERROR);
        }
    }

    public Map<String, Object> sendPostMethod(String url, HttpHeaders headers, String body) {
        try {
            return Objects.requireNonNull(
                    restClient.post()
                            .uri(url)
                            .headers(httpHeaders -> httpHeaders.addAll(headers))
                            .contentType(APPLICATION_JSON)
                            .body(body)
                            .retrieve()
                            .onStatus(HttpStatusCode::is4xxClientError, (request, response) -> {
                                throw new CommonException(ErrorCode.INTERNAL_SERVER_ERROR);
                            })
                            .onStatus(HttpStatusCode::is5xxServerError, (request, response) -> {
                                throw new CommonException(ErrorCode.INTERNAL_SERVER_ERROR);
                            })
                            .toEntity(Map.class)
                            .getBody()
            );
        } catch (ResourceAccessException e) {
            // 타임아웃의 경우 ResourceAccessException의 원인이 SocketTimeoutException임
            if (e.getCause() instanceof SocketTimeoutException) {
                throw new CommonException(ErrorCode.EXTERNAL_SERVER_TIMEOUT);
            }
            throw new CommonException(ErrorCode.REST_CLIENT_ERROR);
        } catch (Exception e) {
            throw new CommonException(ErrorCode.REST_CLIENT_ERROR);
        }
    }

    // TODO: 위아래 통합
    public JSONObject sendPost(String url, HttpHeaders headers, String body) {
        try {
            return new JSONObject(Objects.requireNonNull(restClient.post()
                    .uri(url)
                    .headers(httpHeaders -> httpHeaders.addAll(headers))
                    .contentType(APPLICATION_JSON)
                    .body(body)
                    .retrieve()
                    .onStatus(HttpStatusCode::is4xxClientError, (request, response) -> {
                        String errorBody = "";
                        try (InputStream bodyStream = response.getBody()) {
                            errorBody = new BufferedReader(
                                    new InputStreamReader(bodyStream, StandardCharsets.UTF_8))
                                    .lines()
                                    .collect(Collectors.joining("\n"));
                            log.error("{}", errorBody);

                            JSONParser parser = new JSONParser();
                            JSONObject errorJson = (JSONObject) parser.parse(errorBody);
                            if (errorJson.containsKey("code")) {
                                String errorCodeStr = (String) errorJson.get("code");
                                ErrorCode mappedError = mapExternalErrorCode(errorCodeStr);
                                if (mappedError != null) {
                                    throw new CommonException(mappedError);
                                }
                            }
                        } catch (IOException | ParseException e) {
                            log.error("Response body를 읽거나 파싱하는 중 오류 발생", e);
                        }
                        throw new CommonException(ErrorCode.INVALID_ARGUMENT);
                    })
                    .onStatus(HttpStatusCode::is5xxServerError, (request, response) -> {
                        String errorBody = "";
                        try (InputStream bodyStream = response.getBody()) {
                            errorBody = new BufferedReader(
                                    new InputStreamReader(bodyStream, StandardCharsets.UTF_8))
                                    .lines()
                                    .collect(Collectors.joining("\n"));
                            log.error("{}", errorBody);

                            JSONParser parser = new JSONParser();
                            JSONObject errorJson = (JSONObject) parser.parse(errorBody);
                            if (errorJson.containsKey("code")) {
                                String errorCodeStr = (String) errorJson.get("code");
                                ErrorCode mappedError = mapExternalErrorCode(errorCodeStr);
                                if (mappedError != null) {
                                    throw new CommonException(mappedError);
                                }
                            }
                        } catch (IOException | ParseException e) {
                            log.error("Response body를 읽거나 파싱하는 중 오류 발생", e);
                        }
                        throw new CommonException(ErrorCode.REST_CLIENT_ERROR);
                    })
                    .toEntity(JSONObject.class).getBody()));
        } catch (ResourceAccessException e) {
            // 타임아웃의 경우 ResourceAccessException의 원인이 SocketTimeoutException임
            if (e.getCause() instanceof SocketTimeoutException) {
                throw new CommonException(ErrorCode.EXTERNAL_SERVER_TIMEOUT);
            }
            throw new CommonException(ErrorCode.REST_CLIENT_ERROR);
        } catch (Exception e) {
            throw new CommonException(ErrorCode.REST_CLIENT_ERROR);
        }
    }
    /**
     * 외부 API에서 전달받은 에러 코드(externalCode)를 내부 ErrorCode로 매핑합니다.
     * 모든 외부 API 에러 코드를 여기에 정의합니다.
     */
    private static ErrorCode mapExternalErrorCode(String externalCode) {
        return switch (externalCode) {
            case "ALREADY_PROCESSED_PAYMENT" -> ErrorCode.ALREADY_PROCESSED_PAYMENT;
            case "PROVIDER_ERROR" -> ErrorCode.PROVIDER_ERROR;
            case "EXCEED_MAX_CARD_INSTALLMENT_PLAN" -> ErrorCode.EXCEED_MAX_CARD_INSTALLMENT_PLAN;
            case "INVALID_REQUEST" -> ErrorCode.INVALID_ARGUMENT;
            case "NOT_ALLOWED_POINT_USE" -> ErrorCode.NOT_ALLOWED_POINT_USE;
            case "INVALID_API_KEY" -> ErrorCode.INVALID_API_KEY;
            case "INVALID_REJECT_CARD" -> ErrorCode.INVALID_REJECT_CARD;
            case "BELOW_MINIMUM_AMOUNT" -> ErrorCode.BELOW_MINIMUM_AMOUNT;
            case "INVALID_CARD_EXPIRATION" -> ErrorCode.INVALID_CARD_EXPIRATION;
            case "INVALID_STOPPED_CARD" -> ErrorCode.INVALID_STOPPED_CARD;
            case "EXCEED_MAX_DAILY_PAYMENT_COUNT" -> ErrorCode.EXCEED_MAX_DAILY_PAYMENT_COUNT;
            case "NOT_SUPPORTED_INSTALLMENT_PLAN_CARD_OR_MERCHANT" ->
                    ErrorCode.NOT_SUPPORTED_INSTALLMENT_PLAN_CARD_OR_MERCHANT;
            case "INVALID_CARD_INSTALLMENT_PLAN" -> ErrorCode.INVALID_CARD_INSTALLMENT_PLAN;
            case "NOT_SUPPORTED_MONTHLY_INSTALLMENT_PLAN" -> ErrorCode.NOT_SUPPORTED_MONTHLY_INSTALLMENT_PLAN;
            case "EXCEED_MAX_PAYMENT_AMOUNT" -> ErrorCode.EXCEED_MAX_PAYMENT_AMOUNT;
            case "NOT_FOUND_TERMINAL_ID" -> ErrorCode.NOT_FOUND_TERMINAL_ID;
            case "INVALID_AUTHORIZE_AUTH" -> ErrorCode.INVALID_AUTHORIZE_AUTH;
            case "INVALID_CARD_LOST_OR_STOLEN" -> ErrorCode.INVALID_CARD_LOST_OR_STOLEN;
            case "RESTRICTED_TRANSFER_ACCOUNT" -> ErrorCode.RESTRICTED_TRANSFER_ACCOUNT;
            case "INVALID_CARD_NUMBER" -> ErrorCode.INVALID_CARD_NUMBER;
            case "INVALID_UNREGISTERED_SUBMALL" -> ErrorCode.INVALID_UNREGISTERED_SUBMALL;
            case "NOT_REGISTERED_BUSINESS" -> ErrorCode.NOT_REGISTERED_BUSINESS;
            case "EXCEED_MAX_ONE_DAY_WITHDRAW_AMOUNT" -> ErrorCode.EXCEED_MAX_ONE_DAY_WITHDRAW_AMOUNT;
            case "EXCEED_MAX_ONE_TIME_WITHDRAW_AMOUNT" -> ErrorCode.EXCEED_MAX_ONE_TIME_WITHDRAW_AMOUNT;
            case "CARD_PROCESSING_ERROR" -> ErrorCode.CARD_PROCESSING_ERROR;
            case "EXCEED_MAX_AMOUNT" -> ErrorCode.EXCEED_MAX_AMOUNT;
            case "INVALID_ACCOUNT_INFO_RE_REGISTER" -> ErrorCode.INVALID_ACCOUNT_INFO_RE_REGISTER;
            case "NOT_AVAILABLE_PAYMENT" -> ErrorCode.NOT_AVAILABLE_PAYMENT;
            case "UNAPPROVED_ORDER_ID" -> ErrorCode.UNAPPROVED_ORDER_ID;
            case "EXCEED_MAX_MONTHLY_PAYMENT_AMOUNT" -> ErrorCode.EXCEED_MAX_MONTHLY_PAYMENT_AMOUNT;
            case "UNAUTHORIZED_KEY" -> ErrorCode.UNAUTHORIZED_KEY;
            case "REJECT_ACCOUNT_PAYMENT" -> ErrorCode.REJECT_ACCOUNT_PAYMENT;
            case "REJECT_CARD_PAYMENT" -> ErrorCode.REJECT_CARD_PAYMENT;
            case "REJECT_CARD_COMPANY" -> ErrorCode.REJECT_CARD_COMPANY;
            case "FORBIDDEN_REQUEST" -> ErrorCode.FORBIDDEN_REQUEST;
            case "REJECT_TOSSPAY_INVALID_ACCOUNT" -> ErrorCode.REJECT_TOSSPAY_INVALID_ACCOUNT;
            case "EXCEED_MAX_AUTH_COUNT" -> ErrorCode.EXCEED_MAX_AUTH_COUNT;
            case "EXCEED_MAX_ONE_DAY_AMOUNT" -> ErrorCode.EXCEED_MAX_ONE_DAY_AMOUNT;
            case "NOT_AVAILABLE_BANK" -> ErrorCode.NOT_AVAILABLE_BANK;
            case "INVALID_PASSWORD" -> ErrorCode.INVALID_PASSWORD;
            case "INCORRECT_BASIC_AUTH_FORMAT" -> ErrorCode.INCORRECT_BASIC_AUTH_FORMAT;
            case "FDS_ERROR" -> ErrorCode.FDS_ERROR;
            case "NOT_FOUND_PAYMENT" -> ErrorCode.NOT_FOUND_PAYMENT;
            case "NOT_FOUND_PAYMENT_SESSION" -> ErrorCode.NOT_FOUND_PAYMENT_SESSION;
            case "FAILED_PAYMENT_INTERNAL_SYSTEM_PROCESSING" -> ErrorCode.FAILED_PAYMENT_INTERNAL_SYSTEM_PROCESSING;
            case "FAILED_INTERNAL_SYSTEM_PROCESSING" -> ErrorCode.FAILED_INTERNAL_SYSTEM_PROCESSING;
            case "UNKNOWN_PAYMENT_ERROR" -> ErrorCode.UNKNOWN_PAYMENT_ERROR;
            case "ALREADY_CANCELED_PAYMENT" -> ErrorCode.ALREADY_CANCELED_PAYMENT;
            case "INVALID_REFUND_ACCOUNT_INFO" -> ErrorCode.INVALID_REFUND_ACCOUNT_INFO;
            case "EXCEED_CANCEL_AMOUNT_DISCOUNT_AMOUNT" -> ErrorCode.EXCEED_CANCEL_AMOUNT_DISCOUNT_AMOUNT;
            case "INVALID_REFUND_ACCOUNT_NUMBER" -> ErrorCode.INVALID_REFUND_ACCOUNT_NUMBER;
            case "INVALID_BANK" -> ErrorCode.INVALID_BANK;
            case "NOT_MATCHES_REFUNDABLE_AMOUNT" -> ErrorCode.NOT_MATCHES_REFUNDABLE_AMOUNT;
            case "REFUND_REJECTED" -> ErrorCode.REFUND_REJECTED;
            case "ALREADY_REFUND_PAYMENT" -> ErrorCode.ALREADY_REFUND_PAYMENT;
            case "FORBIDDEN_BANK_REFUND_REQUEST" -> ErrorCode.FORBIDDEN_BANK_REFUND_REQUEST;
            case "NOT_CANCELABLE_AMOUNT" -> ErrorCode.NOT_CANCELABLE_AMOUNT;
            case "FORBIDDEN_CONSECUTIVE_REQUEST" -> ErrorCode.FORBIDDEN_CONSECUTIVE_REQUEST;
            case "NOT_CANCELABLE_PAYMENT" -> ErrorCode.NOT_CANCELABLE_PAYMENT;
            case "EXCEED_MAX_REFUND_DUE" -> ErrorCode.EXCEED_MAX_REFUND_DUE;
            case "NOT_ALLOWED_PARTIAL_REFUND_WAITING_DEPOSIT" -> ErrorCode.NOT_ALLOWED_PARTIAL_REFUND_WAITING_DEPOSIT;
            case "NOT_ALLOWED_PARTIAL_REFUND" -> ErrorCode.NOT_ALLOWED_PARTIAL_REFUND;
            case "NOT_CANCELABLE_PAYMENT_FOR_DORMANT_USER" -> ErrorCode.NOT_CANCELABLE_PAYMENT_FOR_DORMANT_USER;
            case "FAILED_REFUND_PROCESS" -> ErrorCode.FAILED_REFUND_PROCESS;
            case "FAILED_METHOD_HANDLING_CANCEL" -> ErrorCode.FAILED_METHOD_HANDLING_CANCEL;
            case "FAILED_PARTIAL_REFUND" -> ErrorCode.FAILED_PARTIAL_REFUND;
            case "COMMON_ERROR" -> ErrorCode.COMMON_ERROR;
            default -> ErrorCode.INVALID_ARGUMENT;
        };
    }
}
