package com.tookscan.tookscan.core.utility;

import static org.springframework.http.MediaType.APPLICATION_JSON;

import com.tookscan.tookscan.core.exception.error.ErrorCode;
import com.tookscan.tookscan.core.exception.type.CommonException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import net.minidev.json.parser.ParseException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
@Slf4j
public class RestClientUtil {

    private final RestClient restClient = RestClient.create();

    public Map<String, Object> sendGetMethod(String url, HttpHeaders headers) {
        return Objects.requireNonNull(
                restClient.get()
                        .uri(url)
                        .headers(httpHeaders -> httpHeaders.addAll(headers))
                        .retrieve()
                        .onStatus(HttpStatusCode::is4xxClientError, (request, response) -> {
                            throw new CommonException(ErrorCode.INTERNAL_SERVER_ERROR);
                        })
                        .onStatus(HttpStatusCode::is5xxServerError, (request, response) -> {
                            throw new CommonException(ErrorCode.INTERNAL_SERVER_ERROR);
                        })
                        // toEntity(...) 말고 toEntity(Map.class)도 가능
                        .toEntity(Map.class)
                        .getBody()
        );
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
        } catch (Exception e) {
            throw new RuntimeException("Error sending POST request", e);
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
                        throw new CommonException(ErrorCode.INTERNAL_SERVER_ERROR);
                    })
                    .toEntity(JSONObject.class).getBody()));
        } catch (Exception e) {
            if (e instanceof CommonException) {
                throw (CommonException) e;
            }
            throw new CommonException(ErrorCode.REST_CLIENT_ERROR);
        }
    }
    /**
     * 외부 API에서 전달받은 에러 코드(externalCode)를 내부 ErrorCode로 매핑합니다.
     * 모든 외부 API 에러 코드를 여기에 정의합니다.
     */
    private static ErrorCode mapExternalErrorCode(String externalCode) {
        switch (externalCode) {
            case "ALREADY_PROCESSED_PAYMENT":
                return ErrorCode.ALREADY_PROCESSED_PAYMENT;
            case "PROVIDER_ERROR":
                return ErrorCode.PROVIDER_ERROR;
            case "EXCEED_MAX_CARD_INSTALLMENT_PLAN":
                return ErrorCode.EXCEED_MAX_CARD_INSTALLMENT_PLAN;
            case "INVALID_REQUEST":
                return ErrorCode.INVALID_ARGUMENT;
            case "NOT_ALLOWED_POINT_USE":
                return ErrorCode.NOT_ALLOWED_POINT_USE;
            case "INVALID_API_KEY":
                return ErrorCode.INVALID_API_KEY;
            case "INVALID_REJECT_CARD":
                return ErrorCode.INVALID_REJECT_CARD;
            case "BELOW_MINIMUM_AMOUNT":
                return ErrorCode.BELOW_MINIMUM_AMOUNT;
            case "INVALID_CARD_EXPIRATION":
                return ErrorCode.INVALID_CARD_EXPIRATION;
            case "INVALID_STOPPED_CARD":
                return ErrorCode.INVALID_STOPPED_CARD;
            case "EXCEED_MAX_DAILY_PAYMENT_COUNT":
                return ErrorCode.EXCEED_MAX_DAILY_PAYMENT_COUNT;
            case "NOT_SUPPORTED_INSTALLMENT_PLAN_CARD_OR_MERCHANT":
                return ErrorCode.NOT_SUPPORTED_INSTALLMENT_PLAN_CARD_OR_MERCHANT;
            case "INVALID_CARD_INSTALLMENT_PLAN":
                return ErrorCode.INVALID_CARD_INSTALLMENT_PLAN;
            case "NOT_SUPPORTED_MONTHLY_INSTALLMENT_PLAN":
                return ErrorCode.NOT_SUPPORTED_MONTHLY_INSTALLMENT_PLAN;
            case "EXCEED_MAX_PAYMENT_AMOUNT":
                return ErrorCode.EXCEED_MAX_PAYMENT_AMOUNT;
            case "NOT_FOUND_TERMINAL_ID":
                return ErrorCode.NOT_FOUND_TERMINAL_ID;
            case "INVALID_AUTHORIZE_AUTH":
                return ErrorCode.INVALID_AUTHORIZE_AUTH;
            case "INVALID_CARD_LOST_OR_STOLEN":
                return ErrorCode.INVALID_CARD_LOST_OR_STOLEN;
            case "RESTRICTED_TRANSFER_ACCOUNT":
                return ErrorCode.RESTRICTED_TRANSFER_ACCOUNT;
            case "INVALID_CARD_NUMBER":
                return ErrorCode.INVALID_CARD_NUMBER;
            case "INVALID_UNREGISTERED_SUBMALL":
                return ErrorCode.INVALID_UNREGISTERED_SUBMALL;
            case "NOT_REGISTERED_BUSINESS":
                return ErrorCode.NOT_REGISTERED_BUSINESS;
            case "EXCEED_MAX_ONE_DAY_WITHDRAW_AMOUNT":
                return ErrorCode.EXCEED_MAX_ONE_DAY_WITHDRAW_AMOUNT;
            case "EXCEED_MAX_ONE_TIME_WITHDRAW_AMOUNT":
                return ErrorCode.EXCEED_MAX_ONE_TIME_WITHDRAW_AMOUNT;
            case "CARD_PROCESSING_ERROR":
                return ErrorCode.CARD_PROCESSING_ERROR;
            case "EXCEED_MAX_AMOUNT":
                return ErrorCode.EXCEED_MAX_AMOUNT;
            case "INVALID_ACCOUNT_INFO_RE_REGISTER":
                return ErrorCode.INVALID_ACCOUNT_INFO_RE_REGISTER;
            case "NOT_AVAILABLE_PAYMENT":
                return ErrorCode.NOT_AVAILABLE_PAYMENT;
            case "UNAPPROVED_ORDER_ID":
                return ErrorCode.UNAPPROVED_ORDER_ID;
            case "EXCEED_MAX_MONTHLY_PAYMENT_AMOUNT":
                return ErrorCode.EXCEED_MAX_MONTHLY_PAYMENT_AMOUNT;
            case "UNAUTHORIZED_KEY":
                return ErrorCode.UNAUTHORIZED_KEY;
            case "REJECT_ACCOUNT_PAYMENT":
                return ErrorCode.REJECT_ACCOUNT_PAYMENT;
            case "REJECT_CARD_PAYMENT":
                return ErrorCode.REJECT_CARD_PAYMENT;
            case "REJECT_CARD_COMPANY":
                return ErrorCode.REJECT_CARD_COMPANY;
            case "FORBIDDEN_REQUEST":
                return ErrorCode.FORBIDDEN_REQUEST;
            case "REJECT_TOSSPAY_INVALID_ACCOUNT":
                return ErrorCode.REJECT_TOSSPAY_INVALID_ACCOUNT;
            case "EXCEED_MAX_AUTH_COUNT":
                return ErrorCode.EXCEED_MAX_AUTH_COUNT;
            case "EXCEED_MAX_ONE_DAY_AMOUNT":
                return ErrorCode.EXCEED_MAX_ONE_DAY_AMOUNT;
            case "NOT_AVAILABLE_BANK":
                return ErrorCode.NOT_AVAILABLE_BANK;
            case "INVALID_PASSWORD":
                return ErrorCode.INVALID_PASSWORD;
            case "INCORRECT_BASIC_AUTH_FORMAT":
                return ErrorCode.INCORRECT_BASIC_AUTH_FORMAT;
            case "FDS_ERROR":
                return ErrorCode.FDS_ERROR;
            case "NOT_FOUND_PAYMENT":
                return ErrorCode.NOT_FOUND_PAYMENT;
            case "NOT_FOUND_PAYMENT_SESSION":
                return ErrorCode.NOT_FOUND_PAYMENT_SESSION;
            case "FAILED_PAYMENT_INTERNAL_SYSTEM_PROCESSING":
                return ErrorCode.FAILED_PAYMENT_INTERNAL_SYSTEM_PROCESSING;
            case "FAILED_INTERNAL_SYSTEM_PROCESSING":
                return ErrorCode.FAILED_INTERNAL_SYSTEM_PROCESSING;
            case "UNKNOWN_PAYMENT_ERROR":
                return ErrorCode.UNKNOWN_PAYMENT_ERROR;
            default:
                return ErrorCode.INVALID_ARGUMENT;
        }
    }
}
