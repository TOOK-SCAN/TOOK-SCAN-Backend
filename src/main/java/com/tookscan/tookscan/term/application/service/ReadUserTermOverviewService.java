package com.tookscan.tookscan.term.application.service;

import com.tookscan.tookscan.term.presentation.dto.response.ReadUserTermOverviewResponseDto;
import com.tookscan.tookscan.term.application.usecase.ReadUserTermOverviewUseCase;
import com.tookscan.tookscan.term.domain.type.ETermType;
import com.tookscan.tookscan.term.repository.TermRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReadUserTermOverviewService implements ReadUserTermOverviewUseCase {

    private final TermRepository termRepository;

    @Override
    @Transactional(readOnly = true)
    public ReadUserTermOverviewResponseDto execute(String type) {
        ETermType eType = ETermType.fromString(type);

        return ReadUserTermOverviewResponseDto.fromEntities(termRepository.findAllByTypeOrElseThrow(eType));
    }
}
