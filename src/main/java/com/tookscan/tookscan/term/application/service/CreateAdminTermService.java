package com.tookscan.tookscan.term.application.service;

import com.tookscan.tookscan.term.presentation.dto.request.CreateAdminTermRequestDto;
import com.tookscan.tookscan.term.presentation.dto.response.CreateAdminTermResponseDto;
import com.tookscan.tookscan.term.application.usecase.CreateAdminTermUseCase;
import com.tookscan.tookscan.term.domain.Term;
import com.tookscan.tookscan.term.domain.service.TermService;
import com.tookscan.tookscan.term.domain.type.ETermType;
import com.tookscan.tookscan.term.repository.TermRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CreateAdminTermService implements CreateAdminTermUseCase {

    private final TermRepository termRepository;

    private final TermService termService;

    @Override
    @Transactional
    public CreateAdminTermResponseDto execute(CreateAdminTermRequestDto requestDto) {

        List<Term> terms = termRepository.findAllByTypeOrElseThrow(
                ETermType.fromString(requestDto.type())
        );

        // Term 생성
        Term term = termService.createTerm(
                requestDto.type(),
                requestDto.title(),
                requestDto.content(),
                requestDto.isRequired(),
                requestDto.isVisible(),
                terms.size() + 1
        );

        term = termRepository.saveAndReturn(term);

        return CreateAdminTermResponseDto.of(term.getId());
    }
}
