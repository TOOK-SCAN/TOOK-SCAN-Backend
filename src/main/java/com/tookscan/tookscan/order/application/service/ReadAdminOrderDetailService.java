package com.tookscan.tookscan.order.application.service;

import com.tookscan.tookscan.order.application.usecase.ReadAdminOrderDetailUseCase;
import com.tookscan.tookscan.order.domain.Document;
import com.tookscan.tookscan.order.domain.Order;
import com.tookscan.tookscan.order.domain.Pdf;
import com.tookscan.tookscan.order.presentation.dto.response.ReadAdminOrderDetailResponseDto;
import com.tookscan.tookscan.order.repository.OrderRepository;
import com.tookscan.tookscan.order.repository.PdfRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReadAdminOrderDetailService implements ReadAdminOrderDetailUseCase {

    private final OrderRepository orderRepository;
    private final PdfRepository pdfRepository;

    @Override
    @Transactional(readOnly = true)
    public ReadAdminOrderDetailResponseDto execute(Long orderId) {
        Order order = orderRepository.findByIdWithDocumentsAndDeliveryOrElseThrow(orderId);
        Map<Document, List<Pdf>> documentPdfsMap = order.getDocuments().stream()
                .collect(Collectors.toMap(
                        document -> document,
                        document -> pdfRepository.findAllByDocumentId(document.getId())
                ));

        return ReadAdminOrderDetailResponseDto.fromEntity(order, documentPdfsMap);
    }
}
