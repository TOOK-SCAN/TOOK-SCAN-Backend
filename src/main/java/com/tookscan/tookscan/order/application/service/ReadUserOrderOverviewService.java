package com.tookscan.tookscan.order.application.service;

import com.tookscan.tookscan.account.domain.User;
import com.tookscan.tookscan.account.repository.UserRepository;
import com.tookscan.tookscan.order.presentation.dto.response.ReadUserOrderOverviewResponseDto;
import com.tookscan.tookscan.order.application.usecase.ReadUserOrderOverviewUseCase;
import com.tookscan.tookscan.order.domain.Order;
import com.tookscan.tookscan.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReadUserOrderOverviewService implements ReadUserOrderOverviewUseCase {

    private final UserRepository userRepository;
    private final OrderRepository orderRepository;

    @Override
    @Transactional(readOnly = true)
    public ReadUserOrderOverviewResponseDto execute(UUID accountId, Integer page, Integer size, String sort, String search, String direction) {
        // 사용자 조회
        User user = userRepository.findByIdOrElseThrow(accountId);

        PageRequest pageRequest = PageRequest.of(page - 1, size, Sort.by(Direction.fromString(direction), sort));

        // 주문 조회
        Page<Order> orders = orderRepository.findAllByUserAndSearchOrElseNull(user, search, pageRequest);

        return ReadUserOrderOverviewResponseDto.fromEntity(orders);
    }

}
