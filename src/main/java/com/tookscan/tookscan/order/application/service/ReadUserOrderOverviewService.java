package com.tookscan.tookscan.order.application.service;

import com.tookscan.tookscan.account.domain.User;
import com.tookscan.tookscan.account.repository.UserRepository;
import com.tookscan.tookscan.order.application.usecase.ReadUserOrderOverviewUseCase;
import com.tookscan.tookscan.order.domain.Order;
import com.tookscan.tookscan.order.domain.type.EOrderStatus;
import com.tookscan.tookscan.order.domain.type.EScanStatus;
import com.tookscan.tookscan.order.presentation.dto.response.ReadUserOrderOverviewResponseDto;
import com.tookscan.tookscan.order.repository.OrderRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReadUserOrderOverviewService implements ReadUserOrderOverviewUseCase {

    private final UserRepository userRepository;
    private final OrderRepository orderRepository;

    @Override
    @Transactional(readOnly = true)
    public ReadUserOrderOverviewResponseDto execute(UUID accountId, Integer page, Integer size, String sort,
                                                    String search, String direction,
                                                    String startDate, String endDate, EOrderStatus orderStatus) {
        // 사용자 조회
        User user = userRepository.findByIdOrElseThrow(accountId);

        PageRequest pageRequest = PageRequest.of(page - 1, size, Sort.by(Direction.fromString(direction), sort));

        // 주문 조회
        List<EOrderStatus> orderStatusList = Optional.ofNullable(orderStatus)
                .map(EOrderStatus::getDisplayList)
                .orElse(null);

        Page<Order> orders = orderRepository.findAllByUserAndSearchAndOrderStatusInOrElseNull(user, search, pageRequest,
                startDate,
                endDate, orderStatusList, sort, Direction.fromString(direction));

        // 주문 상태 카운트
        Integer scanWaitingCount = orderRepository.countByUserAndOrderStatusIn(user, EOrderStatus.getScanStatusList(
                EScanStatus.WAITING));
        Integer scanInProgressCount = orderRepository.countByUserAndOrderStatusIn(user,
                EOrderStatus.getScanStatusList(EScanStatus.IN_PROGRESS));
        Integer scanCompletedCount = orderRepository.countByUserAndOrderStatusIn(user,
                EOrderStatus.getScanStatusList(EScanStatus.COMPLETED));

        return ReadUserOrderOverviewResponseDto.of(orders, scanWaitingCount, scanInProgressCount,
                scanCompletedCount);
    }

}
