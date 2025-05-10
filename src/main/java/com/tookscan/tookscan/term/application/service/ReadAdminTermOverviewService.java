package com.tookscan.tookscan.term.application.service;

import com.tookscan.tookscan.term.presentation.dto.response.ReadAdminTermOverviewResponseDto;
import com.tookscan.tookscan.term.application.usecase.ReadAdminTermOverviewUseCase;
import com.tookscan.tookscan.term.domain.type.ETermType;
import com.tookscan.tookscan.term.repository.TermRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReadAdminTermOverviewService implements ReadAdminTermOverviewUseCase {

    private final TermRepository termRepository;

    @Override
    @Transactional(readOnly = true)
    public ReadAdminTermOverviewResponseDto execute(String type) {
        ETermType eType = ETermType.fromString(type);

        return ReadAdminTermOverviewResponseDto.fromEntities(termRepository.findAllByTypeOrElseThrow(eType));
    }

}
