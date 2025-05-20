package com.tookscan.tookscan.order.repository.impl;

import com.tookscan.tookscan.core.exception.error.ErrorCode;
import com.tookscan.tookscan.core.exception.type.CommonException;
import com.tookscan.tookscan.order.domain.Document;
import com.tookscan.tookscan.order.repository.DocumentRepository;
import com.tookscan.tookscan.order.repository.mysql.DocumentJpaRepository;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class DocumentRepositoryImpl implements DocumentRepository {

    private final DocumentJpaRepository documentJpaRepository;

    @Override
    public void save(Document document) {
        documentJpaRepository.save(document);
    }

    @Override
    public void deleteByIdOrElseThrow(Long id) {
        documentJpaRepository.findById(id)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_DOCUMENT, "문서 ID: " + id));
        documentJpaRepository.deleteById(id);
    }
    
    @Override
    public Document findByIdOrElseThrow(Long id) {
        return documentJpaRepository.findById(id)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_DOCUMENT, "문서 ID: " + id));
    }

    @Override
    public List<Document> findAllByIdsOrElseThrow(List<Long> ids) {
        List<Document> documents = documentJpaRepository.findAllByIdIn(ids);

        if (documents.size() != ids.size()) {
            Set<Long> foundIds = documents.stream()
                    .map(Document::getId)
                    .collect(Collectors.toSet());

            List<Long> notFoundIds = ids.stream()
                    .filter(id -> !foundIds.contains(id))
                    .toList();

            throw new CommonException(ErrorCode.NOT_FOUND_DOCUMENT, "문서 ID: " + notFoundIds);
        }

        return documents;
    }

    @Override
    public void delete(Document document) {
        documentJpaRepository.delete(document);
    }
}
