package com.tookscan.tookscan.term.application.service;

import com.tookscan.tookscan.core.exception.error.ErrorCode;
import com.tookscan.tookscan.core.exception.type.CommonException;
import com.tookscan.tookscan.term.presentation.dto.request.UpdateAdminTermRequestDto;
import com.tookscan.tookscan.term.application.usecase.UpdateAdminTermUseCase;
import com.tookscan.tookscan.term.domain.Term;
import com.tookscan.tookscan.term.domain.service.TermService;
import com.tookscan.tookscan.term.domain.type.ETermType;
import com.tookscan.tookscan.term.repository.TermRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UpdateAdminTermService implements UpdateAdminTermUseCase {

    private final TermRepository termRepository;
    private final TermService termService;

    @Override
    @Transactional
    public void execute(UpdateAdminTermRequestDto requestDto) {
        
        if(requestDto.terms().stream()
                .map(UpdateAdminTermRequestDto.TermInfoDto::type)
                .distinct() // 중복 제거
                .count() > 1) {
            throw new CommonException(ErrorCode.TYPE_COEXISTENCE_ERROR);
        }

        List<Term> terms = termRepository.findAllByTypeOrElseThrow(
                requestDto.terms().get(0).type()
        );

        for (Term term : terms) {
            if (requestDto.terms().stream().noneMatch(dto -> dto.id().equals(term.getId()))) {
                termRepository.deleteById(term.getId());
            }
        }

        processUpdateTerms(requestDto);
    }

    private void processUpdateTerms(UpdateAdminTermRequestDto requestDto) {
        List<Term> terms = termRepository.findAllByTypeOrElseThrow(ETermType.SIGN_UP);
        validateSortOrder(requestDto, terms.size());

        requestDto.terms().forEach(dto -> {
            Term term = terms.stream()
                    .filter(t -> t.getId().equals(dto.id()))
                    .findFirst()
                    .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_TERM));

            termService.updateTerm(
                    term, dto.title(), dto.content(),
                    dto.isRequired(), dto.isVisible(), dto.sortOrder()
            );
            termRepository.save(term);
        });
    }

    private void validateSortOrder(UpdateAdminTermRequestDto requestDto, int size) {
        Set<Integer> uniqueOrders = new HashSet<>();

        requestDto.terms().forEach(dto -> {
            int sortOrder = dto.sortOrder();
            if (sortOrder < 1 || sortOrder > size) {
                throw new CommonException(ErrorCode.SORT_ORDER_OUT_OF_RANGE);
            }
            if (!uniqueOrders.add(sortOrder)) {
                throw new CommonException(ErrorCode.SORT_ORDER_DUPLICATE);
            }
        });

        if (uniqueOrders.size() != size) {
            throw new CommonException(ErrorCode.SORT_ORDER_NOT_CONTINUOUS);
        }
    }
}
