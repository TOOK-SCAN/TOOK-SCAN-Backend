package com.tookscan.tookscan.mail.application.service;

import com.tookscan.tookscan.mail.presentation.dto.response.ReadTestMailStatusResponseDto;
import com.tookscan.tookscan.mail.application.usecase.ReadTestMailStatusUseCase;
import com.tookscan.tookscan.mail.domain.mysql.TestMailStatus;
import com.tookscan.tookscan.mail.repository.TestMailStatusRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReadTestMailStatusService implements ReadTestMailStatusUseCase {

    private final TestMailStatusRepository testMailStatusRepository;

    @Override
    @Transactional(readOnly = true)
    public ReadTestMailStatusResponseDto execute(String email) {
        TestMailStatus testMailStatus = testMailStatusRepository.findByEmailOrElseNull(email);

        return ReadTestMailStatusResponseDto.of(testMailStatus != null && testMailStatus.getIsSent());
    }
}
