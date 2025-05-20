package com.tookscan.tookscan.order.application.service;

import com.tookscan.tookscan.account.domain.User;
import com.tookscan.tookscan.account.repository.UserRepository;
import com.tookscan.tookscan.order.application.usecase.UpdateUserOrderHistoryUseCase;
import com.tookscan.tookscan.order.domain.Document;
import com.tookscan.tookscan.order.domain.Order;
import com.tookscan.tookscan.order.domain.PricePolicy;
import com.tookscan.tookscan.order.domain.service.DocumentService;
import com.tookscan.tookscan.order.domain.service.OrderService;
import com.tookscan.tookscan.order.presentation.dto.request.UpdateUserOrderHistoryRequestDto;
import com.tookscan.tookscan.order.repository.DocumentRepository;
import com.tookscan.tookscan.order.repository.OrderRepository;
import com.tookscan.tookscan.order.repository.PricePolicyRepository;
import java.time.LocalDate;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UpdateUserOrderHistoryService implements UpdateUserOrderHistoryUseCase {
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final DocumentRepository documentRepository;

    private final PricePolicyRepository pricePolicyRepository;

    private final OrderService orderService;
    private final DocumentService documentService;

    @Override
    @Transactional
    public void execute(UUID accountId, Long orderId, UpdateUserOrderHistoryRequestDto requestDto) {
        User user = userRepository.findByIdOrElseThrow(accountId);
        Order order = orderRepository.findByIdOrElseThrow(orderId);
        orderService.validateOrderUser(order, user);

        // 가격 정책 조회
        PricePolicy pricePolicy = pricePolicyRepository.findByStartDateLessThanEqualAndEndDateGreaterThanEqualOrElseThrow(
                LocalDate.now(), LocalDate.now());
        // 문서 삭제
        order.getDocuments().clear();

        // 문서 생성
        requestDto.documents().forEach(doc -> {
            Document document = documentService.createDocument(
                    doc.name(),
                    doc.pagePrediction(),
                    doc.recoveryOption(),
                    order,
                    pricePolicy
            );
            order.getDocuments().add(document);
            documentRepository.save(document);
        });

        order.updateIsOneDayScan(requestDto.isOneDayScan());
        orderRepository.save(order);
    }
}
