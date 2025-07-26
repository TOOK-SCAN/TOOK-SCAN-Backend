package com.tookscan.tookscan.core.listener;

import com.slack.api.Slack;
import com.slack.api.webhook.WebhookResponse;
import com.tookscan.tookscan.core.dto.SendSlackErrorDto;
import com.tookscan.tookscan.core.utility.JsonParseUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class SlackListener {

    @Value("${slack.webhook.url}")
    private String slackWebhookUrl;

    @Async("notificationTaskExecutor")
    @EventListener
    public void sendSlackMessage(SendSlackErrorDto event) {
        String stackTrace = Arrays.stream(event.e().getStackTrace())
                .map(StackTraceElement::toString)
                .collect(Collectors.joining("\n"));

        Slack slack = Slack.getInstance();

        String rawMessage = """
                 ⚠️ *예외 발생*
                 ▪️ 예외 타입: %s
                 ▪️ 메시지: %s
                 ▪️ 발생 위치:\s
                 %s
                \s""".formatted(
                event.e().getClass().getSimpleName(),
                event.e().getMessage(),
                stackTrace
        );

        // 1999자 초과 시 자르기
        String message = rawMessage.length() > 1999
                ? rawMessage.substring(0, 1980) + "\n... (생략됨)"
                : rawMessage;

        Map<String, String> content = Map.of(
                "text", message
        );

        try {
            WebhookResponse response = slack.send(
                    slackWebhookUrl,
                    JsonParseUtil.convertFromObjectToJson(content)
            );
            System.out.println(response);
        } catch (IOException e) {
            log.error("slack 메시지 발송 중 문제가 발생했습니다.");
            throw new RuntimeException(e);
        }
    }

}
