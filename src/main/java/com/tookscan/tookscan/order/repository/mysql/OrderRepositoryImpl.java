package com.tookscan.tookscan.order.repository.mysql;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.tookscan.tookscan.account.domain.User;
import com.tookscan.tookscan.core.exception.error.ErrorCode;
import com.tookscan.tookscan.core.exception.type.CommonException;
import com.tookscan.tookscan.order.domain.Order;
import com.tookscan.tookscan.order.domain.QOrder;
import com.tookscan.tookscan.order.domain.type.EOrderStatus;
import com.tookscan.tookscan.order.repository.OrderRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class OrderRepositoryImpl implements OrderRepository {

    private final OrderJpaRepository orderJpaRepository;
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public void save(Order order) {
        orderJpaRepository.save(order);
    }

    @Override
    public Order findByIdOrElseThrow(Long id) {
        return orderJpaRepository.findById(id)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_ORDER, "주문 ID: " + id));
    }

    @Override
    public List<Order> findAllByIdOrElseThrow(List<Long> ids) {
        List<Order> orders = orderJpaRepository.findAllById(ids);

        if (orders.size() != ids.size()) {
            Set<Long> foundIds = orders.stream()
                    .map(Order::getId)
                    .collect(Collectors.toSet());

            List<Long> notFoundIds = ids.stream()
                    .filter(id -> !foundIds.contains(id))
                    .toList();

            throw new CommonException(ErrorCode.NOT_FOUND_ORDER, "주문 ID: " + notFoundIds);
        }

        return orders;
    }

    @Override
    public void deleteAll(List<Order> orders) {
        orderJpaRepository.deleteAll(orders);
    }

    @Override
    public Map<EOrderStatus, Integer> findOrderStatusCounts() {
        QOrder order = QOrder.order;

        Map<EOrderStatus, Integer> statusCounts = jpaQueryFactory
                .select(order.orderStatus, order.count().intValue())
                .from(order)
                .groupBy(order.orderStatus)
                .fetch()
                .stream()
                .collect(Collectors.toMap(
                        tuple -> tuple.get(order.orderStatus),
                        tuple -> tuple.get(order.count().intValue())
                ));

        for (EOrderStatus status : EOrderStatus.values()) {
            statusCounts.putIfAbsent(status, 0);
        }

        return statusCounts;
    }

    @Override
    public Page<Long> findOrderSummaries(String startDate, String endDate,
                                         String search, String searchType, String sort, String direction,
                                         Pageable pageable) {
        QOrder order = QOrder.order;

        // 검색 조건 동적 생성
        BooleanExpression predicate = buildPredicate(order, startDate, endDate, search, searchType);

        // 데이터 조회
        List<Long> orderIds = jpaQueryFactory.select(order.id)
                .from(order)
                .where(predicate)
                .orderBy(resolveSort(order, sort, direction))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // 전체 데이터 개수 조회
        long totalCount = Optional.ofNullable(
                jpaQueryFactory.select(order.count())
                        .from(order)
                        .where(predicate)
                        .fetchOne()
        ).orElse(0L);

        // Page 객체 생성
        return new PageImpl<>(orderIds, pageable, totalCount);
    }

    @Override
    public Order findByOrderNumber(String orderNumber) {
        return orderJpaRepository.findByOrderNumber(orderNumber)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_ORDER, "주문 번호: " + orderNumber));
    }

    @Override
    public List<Order> findAllByUserIds(List<UUID> userIds) {
        return orderJpaRepository.findAllByUserIds(userIds);
    }

    @Override
    public List<Order> findAllWithDocumentsByIdIn(List<Long> ids) {
        return orderJpaRepository.findAllWithDocumentsByIdIn(ids);
    }

    @Override
    public Page<Order> findAllByUserAndSearch(User user, String search, Pageable pageable) {
        return orderJpaRepository.findAllByUserAndSearch(user, search, pageable);
    }

    private BooleanExpression buildPredicate(QOrder order, String startDate, String endDate, String search,
                                             String searchType) {
        BooleanExpression predicate = order.isNotNull();

        predicate = addDateRangePredicate(predicate, order, startDate, endDate);

        predicate = addSearchPredicate(predicate, order, search, searchType);

        return predicate;
    }

    private BooleanExpression addDateRangePredicate(BooleanExpression predicate, QOrder order, String startDate,
                                                    String endDate) {
        if (startDate != null) {
            predicate = predicate.and(order.createdAt.goe(LocalDate.parse(startDate).atStartOfDay()));
        }
        if (endDate != null) {
            predicate = predicate.and(order.createdAt.loe(LocalDate.parse(endDate).atStartOfDay()));
        }
        return predicate;
    }

    private BooleanExpression addSearchPredicate(BooleanExpression predicate, QOrder order, String search,
                                                 String searchType) {
        if (search == null || searchType == null) {
            return predicate;
        }

        return switch (searchType) {
            case "order-number" -> predicate.and(order.orderNumber.containsIgnoreCase(search));
            case "name" -> predicate.and(order.user.name.containsIgnoreCase(search)
                    .or(order.delivery.receiverName.containsIgnoreCase(search)));
            case "phone-number" -> predicate.and(order.user.phoneNumber.containsIgnoreCase(search)
                    .or(order.delivery.phoneNumber.containsIgnoreCase(search)));
            default -> predicate;
        };
    }

    private OrderSpecifier<?> resolveSort(QOrder order, String sort, String direction) {
        if ("asc".equalsIgnoreCase(direction)) {
            return switch (sort.toLowerCase()) {
                case "order-number" -> order.orderNumber.asc();
                case "created-at" -> order.createdAt.asc();
                default -> order.id.asc();
            };
        } else {
            return switch (sort.toLowerCase()) {
                case "order-number" -> order.orderNumber.desc();
                case "created-at" -> order.createdAt.desc();
                default -> order.id.desc();
            };
        }
    }
}
