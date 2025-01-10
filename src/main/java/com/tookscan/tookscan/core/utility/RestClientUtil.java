package com.tookscan.tookscan.core.utility;

import com.tookscan.tookscan.core.exception.error.ErrorCode;
import com.tookscan.tookscan.core.exception.type.CommonException;
import net.minidev.json.JSONObject;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

import java.util.Objects;

import static org.springframework.http.MediaType.APPLICATION_JSON;

@Component
public class RestClientUtil {

    private final RestClient restClient = RestClient.create();

    public JSONObject sendGetMethod(String url, HttpHeaders headers) {
        return new JSONObject(Objects.requireNonNull(restClient.get()
                .uri(url)
                .headers(httpHeaders -> httpHeaders.addAll(headers))
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (request, response) -> {
                    throw new CommonException(ErrorCode.INVALID_ARGUMENT);
                })
                .onStatus(HttpStatusCode::is5xxServerError, (request, response) -> {
                    throw new CommonException(ErrorCode.INTERNAL_SERVER_ERROR);
                })
                .toEntity(JSONObject.class).getBody()));
    }

    public JSONObject sendPostMethod(String url, HttpHeaders headers, String body) {
        try {
            return new JSONObject(Objects.requireNonNull(restClient.post()
                    .uri(url)
                    .headers(httpHeaders -> httpHeaders.addAll(headers))
                    .contentType(APPLICATION_JSON)
                    .body(body)
                    .retrieve()
                    .onStatus(HttpStatusCode::is4xxClientError, (request, response) -> {
                        throw new CommonException(ErrorCode.INVALID_ARGUMENT);
                    })
                    .onStatus(HttpStatusCode::is5xxServerError, (request, response) -> {
                        throw new CommonException(ErrorCode.INTERNAL_SERVER_ERROR);
                    })
                    .toEntity(JSONObject.class).getBody()));
        } catch (Exception e) {
            throw new RuntimeException("Error sending POST request", e);
        }
    }


}
