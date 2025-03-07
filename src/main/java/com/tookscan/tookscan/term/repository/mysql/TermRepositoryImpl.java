package com.tookscan.tookscan.term.repository.mysql;

import com.tookscan.tookscan.core.exception.error.ErrorCode;
import com.tookscan.tookscan.core.exception.type.CommonException;
import com.tookscan.tookscan.term.domain.Term;
import com.tookscan.tookscan.term.domain.type.ETermType;
import com.tookscan.tookscan.term.repository.TermRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class TermRepositoryImpl implements TermRepository {

    private final TermJpaRepository termJpaRepository;

    @Override
    public void save(Term term) {
        termJpaRepository.save(term);
    }

    @Override
    public Term saveAndReturn(Term term) {
        return termJpaRepository.save(term);
    }

    @Override
    public Term findByIdOrElseThrow(Long id) {
        return termJpaRepository.findById(id)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_TERM));
    }

    @Override
    public List<Term> findAll() {
        return termJpaRepository.findAll();
    }

    @Override
    public List<Term> findAllByTypeOrElseThrow(ETermType type) {
        List<Term> terms = termJpaRepository.findAllByTypeOrderBySortOrderAsc(type);

        if (terms.isEmpty()) {
            throw new CommonException(ErrorCode.NOT_FOUND_TERM);
        }
        return terms;
    }

    @Override
    public void deleteById(Long id) {
        Term term = findByIdOrElseThrow(id);

        termJpaRepository.delete(term);
    }
}
