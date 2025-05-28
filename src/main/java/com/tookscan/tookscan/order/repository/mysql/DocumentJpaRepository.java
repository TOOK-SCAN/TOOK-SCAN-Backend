package com.tookscan.tookscan.order.repository.mysql;

import com.tookscan.tookscan.order.domain.Document;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface DocumentJpaRepository extends JpaRepository<Document, Long> {
    @Query("SELECT d FROM Document d WHERE d.id IN :ids")
    List<Document> findAllByIdIn(List<Long> ids);

    @Query("SELECT d FROM Document d LEFT JOIN FETCH d.pdfs WHERE d.id = :id")
    Optional<Document> findByIdWithPdfs(Long id);
}
