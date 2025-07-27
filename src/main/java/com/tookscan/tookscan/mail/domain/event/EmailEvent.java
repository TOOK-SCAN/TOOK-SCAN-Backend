package com.tookscan.tookscan.mail.domain.event;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class EmailEvent {
    String email;

    public static EmailEvent of(String email) {
        return EmailEvent.builder()
                .email(email)
                .build();
    }
}
