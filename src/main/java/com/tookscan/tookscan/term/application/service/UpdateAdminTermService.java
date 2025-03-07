package com.tookscan.tookscan.term.application.service;

import com.tookscan.tookscan.core.exception.error.ErrorCode;
import com.tookscan.tookscan.core.exception.type.CommonException;
import com.tookscan.tookscan.term.application.dto.request.UpdateAdminTermRequestDto;
import com.tookscan.tookscan.term.application.usecase.UpdateAdminTermUseCase;
import com.tookscan.tookscan.term.domain.Term;
import com.tookscan.tookscan.term.domain.service.TermService;
import com.tookscan.tookscan.term.domain.type.ETermType;
import com.tookscan.tookscan.term.repository.TermRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class UpdateAdminTermService implements UpdateAdminTermUseCase {

    private final TermRepository termRepository;

    private final TermService termService;

    @Override
    @Transactional
    public void execute(UpdateAdminTermRequestDto requestDto) {

        boolean isSignUpChanged = requestDto.terms().stream()
                .anyMatch(dto -> dto.type() == ETermType.SIGN_UP);

        boolean isScanChanged = requestDto.terms().stream()
                .anyMatch(dto -> dto.type() == ETermType.SCAN);

        List<Term> terms = termRepository.findAll();

        List<Term> signupTerms = terms.stream()
                .filter(term -> term.getType() == ETermType.SIGN_UP)
                .toList();
        List<Term> scanTerms = terms.stream()
                .filter(term -> term.getType() == ETermType.SCAN)
                .toList();
        int signupSize = signupTerms.size();
        int scanSize = scanTerms.size();

        Map<Integer, Boolean> checkSignupSortOrder = new HashMap<>();
        Map<Integer, Boolean> checkScanSortOrder = new HashMap<>();

        for (int i = 1; i <= signupSize; i++) {
            checkSignupSortOrder.put(i, false);
        }

        for (int i = 1; i <= scanSize; i++) {
            checkScanSortOrder.put(i, false);
        }

        requestDto.terms().forEach(dto -> {
            if (dto.type() == ETermType.SIGN_UP) {
                if (dto.sortOrder() < 0 || dto.sortOrder() > signupSize) {
                    throw new CommonException(ErrorCode.SORT_ORDER_OUT_OF_RANGE);
                }
                if (checkSignupSortOrder.get(dto.sortOrder())) {
                    throw new CommonException(ErrorCode.SORT_ORDER_DUPLICATE);
                }
                checkSignupSortOrder.put(dto.sortOrder(), true);
            }

            if (dto.type() == ETermType.SCAN) {
                if (dto.sortOrder() < 0 || dto.sortOrder() > scanSize) {
                    throw new CommonException(ErrorCode.SORT_ORDER_OUT_OF_RANGE);
                }
                if (checkScanSortOrder.get(dto.sortOrder())) {
                    throw new CommonException(ErrorCode.SORT_ORDER_DUPLICATE);
                }
                checkScanSortOrder.put(dto.sortOrder(), true);
            }
        });

        if (isSignUpChanged && checkSignupSortOrder.containsValue(false)) {
            throw new CommonException(ErrorCode.SORT_ORDER_NOT_CONTINUOUS);
        }

        if (isScanChanged && checkScanSortOrder.containsValue(false)) {
            throw new CommonException(ErrorCode.SORT_ORDER_NOT_CONTINUOUS);
        }

        requestDto.terms().forEach(dto -> {
            Term term = terms.stream()
                    .filter(t -> t.getId().equals(dto.id()))
                    .findFirst()
                    .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_TERM));
            termService.updateTerm(
                    term,
                    dto.title(),
                    dto.content(),
                    dto.isRequired(),
                    dto.isVisible(),
                    dto.sortOrder()
            );
            termRepository.save(term);
        });
    }
}
