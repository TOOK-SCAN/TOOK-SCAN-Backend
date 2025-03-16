package com.tookscan.tookscan.order.application.service;

import com.tookscan.tookscan.core.utility.S3Util;
import com.tookscan.tookscan.order.application.dto.response.ReadAdminOrderBriefResponseDto;
import com.tookscan.tookscan.order.application.usecase.ReadAdminOrderBriefUseCase;
import com.tookscan.tookscan.order.domain.Order;
import com.tookscan.tookscan.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReadAdminOrderBriefService implements ReadAdminOrderBriefUseCase {

    private final OrderRepository orderRepository;

    private final S3Util s3Util;

    @Override
    @Transactional(readOnly = true)
    public ReadAdminOrderBriefResponseDto execute(Long orderId) {
        Order order = orderRepository.findByIdOrElseThrow(orderId);

        int pdfCount = order.getDocuments().stream()
                .mapToInt(document -> s3Util.doesObjectExist(document) ? 1 : 0)
                .sum();

        String status = order.getDocuments().size() == pdfCount ? "스캔완료" : "스캔중";

        if (pdfCount == 0) {
            status = "스캔대기";
        }

        return ReadAdminOrderBriefResponseDto.of(order, pdfCount, status);
    }

}
