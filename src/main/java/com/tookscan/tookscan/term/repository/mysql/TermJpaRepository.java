package com.tookscan.tookscan.term.repository.mysql;

import com.tookscan.tookscan.term.domain.Term;
import com.tookscan.tookscan.term.domain.type.ETermType;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TermJpaRepository extends JpaRepository<Term, Long> {

    @Query(" SELECT t FROM Term t WHERE t.type = :type ORDER BY t.type, t.sortOrder ASC " )
    List<Term> findAllByTypeOrderBySortOrderAsc(@Param("type") ETermType type);

}
