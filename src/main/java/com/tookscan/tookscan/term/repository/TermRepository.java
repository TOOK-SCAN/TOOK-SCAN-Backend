package com.tookscan.tookscan.term.repository;

import com.tookscan.tookscan.term.domain.Term;
import com.tookscan.tookscan.term.domain.type.ETermType;
import java.util.List;

public interface TermRepository {

    void save(Term term);

    Term findByIdOrElseThrow(Long id);

    List<Term> findAll();

    List<Term> findAllByTypeOrElseThrow(ETermType type);

    void deleteById(Long id);
}
