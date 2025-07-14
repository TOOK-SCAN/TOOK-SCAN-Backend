package com.tookscan.tookscan.account.application.service;

import com.tookscan.tookscan.account.application.usecase.ReadAdminUserOverviewUseCase;
import com.tookscan.tookscan.account.domain.User;
import com.tookscan.tookscan.account.presentation.dto.response.ReadAdminUserOverviewResponseDto;
import com.tookscan.tookscan.account.repository.UserRepository;
import com.tookscan.tookscan.core.dto.PageInfoDto;
import com.tookscan.tookscan.core.utility.DateTimeUtil;
import com.tookscan.tookscan.order.domain.Order;
import com.tookscan.tookscan.order.repository.OrderRepository;
import com.tookscan.tookscan.security.domain.type.ESecurityProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

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
            String provider,
            String startDate,
            String endDate,
            Integer page,
            Integer size,
            String status,
            String sort,
            Direction direction
    ) {

        Pageable pageable = PageRequest.of(page-1, size);

        // provider를 ESecurityProvider로 변환 (null 체크 추가)
        ESecurityProvider securityProvider = null;
        if (provider != null && !provider.trim().isEmpty()) {
            securityProvider = ESecurityProvider.fromString(provider);
        }

        // 페이지네이션 된 userId 페이지 객체 조회
        Page<UUID> userIdsPage = userRepository.findUserIdsByFilters(
                searchType,
                search,
                groupId,
                securityProvider,
                startDate != null ? DateTimeUtil.convertStringToLocalDate(startDate) : null,
                endDate != null ? DateTimeUtil.convertStringToLocalDate(endDate) : null,
                pageable,
                status,
                sort,
                direction
        );

        // 리스트로 변환
        List<UUID> userIds = userIdsPage.stream().toList();

        // Order 리스트 조회
        List<Order> orders = orderRepository.findAllByUserIds(userIds);

        // User 리스트 조회
        List<User> users = userRepository.findByIdsWithDetails(userIds);

        Map<UUID, User> userMap = users.stream()
                .collect(Collectors.toMap(User::getId, Function.identity()));
        List<User> sortedUsers = userIds.stream()
                .map(userMap::get)
                .toList();

        return ReadAdminUserOverviewResponseDto.of(
                sortedUsers,
                orders,
                PageInfoDto.fromEntity(userIdsPage)
        );
    }
}
