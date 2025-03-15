package com.tookscan.tookscan.account.application.service;

import com.tookscan.tookscan.account.application.dto.response.ReadAdminUserOverviewResponseDto;
import com.tookscan.tookscan.account.application.usecase.ReadAdminUserOverviewUseCase;
import com.tookscan.tookscan.account.domain.User;
import com.tookscan.tookscan.account.repository.UserRepository;
import com.tookscan.tookscan.core.dto.PageInfoDto;
import com.tookscan.tookscan.core.utility.DateTimeUtil;
import com.tookscan.tookscan.order.domain.Order;
import com.tookscan.tookscan.order.repository.OrderRepository;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReadAdminUserOverviewService implements ReadAdminUserOverviewUseCase {

    private final UserRepository userRepository;
    private final OrderRepository orderRepository;

    @Override
    @Transactional(readOnly = true)
    public ReadAdminUserOverviewResponseDto execute(
            String searchType,
            String search,
            Long groupId,
            String startDate,
            String endDate,
            Integer page,
            Integer size
    ) {

        Pageable pageable = PageRequest.of(page-1, size);

        // 페이지네이션 된 userId 페이지 객체 조회
        Page<UUID> userIdsPage = userRepository.findUserIdsByFilters(
                searchType,
                search,
                groupId,
                startDate != null ? DateTimeUtil.convertStringToLocalDate(startDate) : null,
                endDate != null ? DateTimeUtil.convertStringToLocalDate(endDate) : null,
                pageable
        );

        // 리스트로 변환
        List<UUID> userIds = userIdsPage.stream().toList();

        // Order 리스트 조회
        List<Order> orders = orderRepository.findAllByUserIds(userIds);

        // User 리스트 조회
        List<User> users = userRepository.findByIdsWithDetails(userIds);

        return ReadAdminUserOverviewResponseDto.of(
                users,
                orders,
                PageInfoDto.fromEntity(userIdsPage)
        );
    }
}
