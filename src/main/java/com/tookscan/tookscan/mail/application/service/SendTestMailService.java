package com.tookscan.tookscan.mail.application.service;

import com.tookscan.tookscan.core.exception.error.ErrorCode;
import com.tookscan.tookscan.core.exception.type.CommonException;
import com.tookscan.tookscan.mail.domain.mysql.TestMailStatus;
import com.tookscan.tookscan.mail.presentation.dto.request.SendTestMailRequestDto;
import com.tookscan.tookscan.mail.application.usecase.SendTestMailUseCase;
import com.tookscan.tookscan.mail.domain.redis.TestMailHistory;
import com.tookscan.tookscan.mail.event.EmailEvent;
import com.tookscan.tookscan.mail.repository.TestMailHistoryRepository;
import com.tookscan.tookscan.mail.repository.TestMailStatusRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SendTestMailService implements SendTestMailUseCase {

    private final ApplicationEventPublisher applicationEventPublisher;
    private final TestMailHistoryRepository testMailHistoryRepository;
    private final TestMailStatusRepository testMailStatusRepository;

    @Override
    public void execute(SendTestMailRequestDto requestDto) {

        TestMailHistory testMailHistory = testMailHistoryRepository.findByIdOrElseNull(requestDto.email());
        TestMailStatus testMailStatus = testMailStatusRepository.findByEmailOrElseNull(requestDto.email());

        if (testMailHistory != null && testMailHistory.getCount() >= 4) {
            throw new CommonException(ErrorCode.TOO_MANY_TEST_MAIL_REQUESTS);
        }

        applicationEventPublisher.publishEvent(EmailEvent.of(requestDto.email()));

        if (testMailHistory == null) {
            testMailHistory = TestMailHistory.builder()
                    .email(requestDto.email())
                    .count(1)
                    .build();
            testMailHistory.updateLastSentAt();
            testMailHistoryRepository.save(testMailHistory);
        } else {
            testMailHistory.incrementCount();
            testMailHistory.updateLastSentAt();
            testMailHistoryRepository.save(testMailHistory);
        }

        if (testMailStatus == null) {
            testMailStatus = TestMailStatus.builder()
                    .email(requestDto.email())
                    .isSent(true)
                    .build();
            testMailStatusRepository.save(testMailStatus);
        }
    }
}
