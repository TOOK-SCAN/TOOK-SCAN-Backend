package com.tookscan.tookscan.order.repository.impl;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.DateTimePath;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.tookscan.tookscan.account.domain.User;
import com.tookscan.tookscan.core.exception.error.ErrorCode;
import com.tookscan.tookscan.core.exception.type.CommonException;
import com.tookscan.tookscan.order.domain.Order;
import com.tookscan.tookscan.order.domain.QDocument;
import com.tookscan.tookscan.order.domain.QOrder;
import com.tookscan.tookscan.order.domain.type.EOrderStatus;
import com.tookscan.tookscan.order.domain.type.ERecoveryOption;
import com.tookscan.tookscan.order.repository.OrderRepository;
import com.tookscan.tookscan.order.repository.mysql.OrderJpaRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
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
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

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
    public void saveAll(List<Order> orders) {
        orderJpaRepository.saveAll(orders);
    }

    @Override
    public Order findByIdOrElseThrow(Long id) {
        return orderJpaRepository.findById(id)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_ORDER, "주문 ID: " + id));
    }

    @Override
    public Integer countByIsAsInProgressTrue() {
        return orderJpaRepository.countByIsAsInProgressTrue();
    }

    @Override
    public Order findWithUserById(Long id) {
        return orderJpaRepository.findWithUserById(id)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_ORDER, "주문 ID: " + id));
    }

    @Override
    public Order findWithUsedCouponByIdOrElseThrow(Long id) {
        return orderJpaRepository.findWithUsedCouponById(id)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_ORDER, "주문 ID: " + id));
    }

    @Override
    public Order findByIdWithDocumentsAndDeliveryOrElseThrow(Long id) {
        return orderJpaRepository.findByIdWithDocumentsAndDelivery(id)
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
    public List<Order> findAllByOrderStatusDateBetweenOrElseThrow(LocalDateTime startDate, LocalDateTime endDate,
                                                                  EOrderStatus orderStatus) {
        List<Order> orders = orderJpaRepository.findAllByOrderStatusDateBetween(startDate, endDate, orderStatus);

        if (orders.isEmpty()) {
            throw new CommonException(ErrorCode.NOT_FOUND_ORDER, "주문 날짜: " + startDate + " ~ " + endDate);
        }

        return orders;
    }

    @Override
    public void deleteAll(List<Order> orders) {
        orderJpaRepository.deleteAll(orders);
    }

    @Override
    public void deleteAllById(List<Long> ids) {
        orderJpaRepository.deleteAllById(ids);
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
                                         String search, String searchType, String sort, Direction direction,
                                         Pageable pageable) {
        QOrder order = QOrder.order;

        // 검색 조건 동적 생성
        BooleanExpression predicate = buildPredicate(order, startDate, endDate, search, searchType);
        predicate = predicate.and(order.orderStatus.eq(EOrderStatus.APPLY_COMPLETED));

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
    public Page<Long> findOrderOverviews(String startDate, String endDate,
                                         String search, String searchType, String sort, Direction direction,
                                         Pageable pageable, EOrderStatus orderStatus, Boolean isOneDayScan,
                                         Boolean hasRecoveryOption, Boolean isAsInProgress, Boolean isInProgress, UUID customerKey) {
        QOrder order = QOrder.order;

        // 검색 조건 동적 생성
        BooleanExpression predicate = buildPredicate(order, startDate, endDate, search, searchType);

        // orderStatus가 null이 아닐 때만 필터링 추가
        if (orderStatus != null) {
            predicate = predicate.and(order.orderStatus.eq(orderStatus));
        }

        // isOneDayScan 필터링
        if (isOneDayScan != null) {
            predicate = predicate.and(order.isOneDayScan.eq(isOneDayScan));
        }

        // hasRecoveryOption 필터링
        if (hasRecoveryOption != null) {
            if (hasRecoveryOption) {
                // documents 중 recoveryOption != DISCARD 인 게 하나라도 있는 주문
                predicate = predicate.and(
                        order.documents.any()
                                .recoveryOption.ne(ERecoveryOption.DISCARD)
                );
            } else {
                // 모든 documents 의 recoveryOption 이 DISCARD 인 주문
                predicate = predicate.and(
                        order.documents.any()
                                .recoveryOption.ne(ERecoveryOption.DISCARD)
                                .not()
                );
            }
        }

        // isAsInProgress 필터링
        if (isAsInProgress != null) {
            predicate = predicate.and(order.isAsInProgress.eq(isAsInProgress));
        }

        if (isInProgress != null) {
            if (isInProgress) {
                // 진행 중인 상태(취소·완료 제외)
                predicate = predicate.and(
                        order.orderStatus.notIn(EOrderStatus.CANCEL, EOrderStatus.ALL_COMPLETED)
                );
            } else {
                // 진행 중이 아닌 상태(취소 혹은 전부 완료)
                predicate = predicate.and(
                        order.orderStatus.in(EOrderStatus.CANCEL, EOrderStatus.ALL_COMPLETED)
                );
            }
        }

        if (customerKey != null) {
            // customerKey가 있는 경우, 해당 키로 필터링
            predicate = predicate.and(order.user.id.eq(customerKey));
        }

        if (direction == null) {
            direction = Direction.DESC; // 기본 정렬 방향
        }

        // 데이터 조회
        List<Order> orders = jpaQueryFactory
                .select(order)
                .from(order)
                .join(order.user).fetchJoin()
                .where(predicate)
                .orderBy(resolveSort(order, sort, direction))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        List <Long> orderIds = orders.stream()
                .map(Order::getId)
                .collect(Collectors.toList());

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
    public Page<Long> findDeliveriesSummaries(String startDate, String endDate, String search, String searchType,
                                              EOrderStatus orderStatus,
                                              Pageable pageable) {
        QOrder order = QOrder.order;

        // 검색 조건 동적 생성
        BooleanExpression predicate = buildPredicate(order, startDate, endDate, search, searchType);
        predicate = predicate.and(order.orderStatus.eq(orderStatus));

        // 데이터 조회
        List<Long> orderIds = jpaQueryFactory.select(order.id)
                .from(order)
                .where(predicate)
                .orderBy(order.createdAt.asc())
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
    public Order findByOrderNumberOrElseThrow(String orderNumber) {
        return orderJpaRepository.findByOrderNumber(orderNumber)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_ORDER, "주문 번호: " + orderNumber));
    }

    @Override
    public Order findWithUserAndDeliveryByOrderNumberOrElseThrow(String orderNumber) {
        return orderJpaRepository.findWithUserAndDeliveryByOrderNumber(orderNumber)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_ORDER, "주문 번호: " + orderNumber));
    }

    @Override
    public List<Order> findAllByUserIds(List<UUID> userIds) {
        return orderJpaRepository.findAllByUserIds(userIds);
    }

    @Override
    public Integer countByUserAndOrderStatusIn(User user, List<EOrderStatus> orderStatuses) {
        return orderJpaRepository.countByUserAndOrderStatusIn(user, orderStatuses);
    }

    @Override
    public List<Order> findAllWithDocumentsByIdIn(List<Long> ids) {
        return orderJpaRepository.findAllWithDocumentsByIdIn(ids);
    }

    @Override
    public List<Order> findAllWithDocumentsAndUserByIdIn(List<Long> ids) {
        return orderJpaRepository.findAllWithDocumentsAndUserByIdIn(ids);
    }

    @Override
    public Page<Order> findAllByUserAndSearchAndOrderStatusInOrElseNull(
            User user,
            String search,
            Pageable pageable,
            String startDate,
            String endDate,
            List<EOrderStatus> orderStatuses,
            String sort,
            Direction direction
    ) {
        QOrder o = QOrder.order;

        // 1) Base predicate: only this user
        BooleanExpression predicate = o.user.eq(user);

        // 2) Search filter
        if (StringUtils.hasText(search)) {
            predicate = predicate.and(
                    o.orderNumber.containsIgnoreCase(search)
                            .or(o.delivery.receiverName.containsIgnoreCase(search))
            );
        }

        // 3) Date range filter
        if (startDate != null) {
            predicate = predicate.and(
                    o.createdAt.goe(LocalDate.parse(startDate).atStartOfDay())
            );
        }
        if (endDate != null) {
            predicate = predicate.and(
                    o.createdAt.loe(LocalDate.parse(endDate).atTime(LocalTime.MAX))
            );
        }

        // 4) Status filter (IN clause)
        if (orderStatuses != null && !orderStatuses.isEmpty()) {
            predicate = predicate.and(o.orderStatus.in(orderStatuses));
        }

        // 5) Fetch paged content sorted by newest first
        List<Order> content = jpaQueryFactory
                .selectFrom(o)
                .where(predicate)
                .orderBy(resolveSort(o, sort, direction))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // 6) Count total
        long total = Optional.ofNullable(
                jpaQueryFactory.select(o.count())
                        .from(o)
                        .where(predicate)
                        .fetchOne()
        ).orElse(0L);

        return new PageImpl<>(content, pageable, total);
    }


    @Override
    public List<Order> findAllByOrderNumberIn(List<String> orderNumber) {
        return orderJpaRepository.findAllByOrderNumberIn(orderNumber);
    }

    @Override
    public List<Order> findAllByCreatedAtBeforeWithEOrderStatus(LocalDateTime dateTime, EOrderStatus status) {
        return orderJpaRepository.findAllByCreatedAtBeforeWithEOrderStatus(dateTime, status);
    }

    @Override
    public Integer countByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate) {
        return orderJpaRepository.countByCreatedAtBetween(startDate, endDate);
    }

    @Override
    public Integer countByCreatedAtBetweenAndOrderStatus(LocalDateTime startDate, LocalDateTime endDate, EOrderStatus orderStatus) {
        return orderJpaRepository.countByCreatedAtBetweenAndOrderStatus(startDate, endDate, orderStatus);
    }

    @Override
    public Integer countByCreatedAtBetweenAndRecoveryOption(LocalDateTime startDate, LocalDateTime endDate, ERecoveryOption recoveryOption) {
        return orderJpaRepository.countByCreatedAtBetweenAndRecoveryOption(startDate, endDate, recoveryOption);
    }

    @Override
    public Map<String, Integer> findMonthlyOrderCounts(LocalDateTime startDate, LocalDateTime endDate) {
        QOrder order = QOrder.order;
        
        return jpaQueryFactory
                .select(
                        Expressions.stringTemplate("DATE_FORMAT({0}, '%Y-%m')", order.createdAt),
                        order.count().intValue()
                )
                .from(order)
                .where(order.createdAt.between(startDate, endDate))
                .groupBy(
                        Expressions.stringTemplate("DATE_FORMAT({0}, '%Y-%m')", order.createdAt)
                )
                .fetch()
                .stream()
                .collect(Collectors.toMap(
                        tuple -> tuple.get(0, String.class),
                        tuple -> tuple.get(1, Integer.class)
                ));
    }

    @Override
    public Map<String, Map<EOrderStatus, Integer>> findMonthlyOrderStatusCounts(LocalDateTime startDate, LocalDateTime endDate) {
        QOrder order = QOrder.order;
        
        return jpaQueryFactory
                .select(
                        Expressions.stringTemplate("DATE_FORMAT({0}, '%Y-%m')", order.createdAt),
                        order.orderStatus,
                        order.count().intValue()
                )
                .from(order)
                .where(order.createdAt.between(startDate, endDate))
                .groupBy(
                        Expressions.stringTemplate("DATE_FORMAT({0}, '%Y-%m')", order.createdAt),
                        order.orderStatus
                )
                .fetch()
                .stream()
                .collect(Collectors.groupingBy(
                        tuple -> tuple.get(0, String.class),
                        Collectors.toMap(
                                tuple -> tuple.get(1, EOrderStatus.class),
                                tuple -> tuple.get(2, Integer.class)
                        )
                ));
    }

    @Override
    public Map<String, Integer> findMonthlyCompanyArrivedCounts(LocalDateTime startDate, LocalDateTime endDate) {
        QOrder order = QOrder.order;

        return jpaQueryFactory
                .select(
                        Expressions.stringTemplate("DATE_FORMAT({0}, '%Y-%m')", order.arrivedAt),
                        order.count().intValue()
                )
                .from(order)
                .where(order.arrivedAt.isNotNull()
                        .and(order.arrivedAt.between(startDate, endDate)))
                .groupBy(Expressions.stringTemplate("DATE_FORMAT({0}, '%Y-%m')", order.arrivedAt))
                .fetch()
                .stream()
                .collect(Collectors.toMap(
                        tuple -> tuple.get(0, String.class),
                        tuple -> tuple.get(1, Integer.class)
                ));
    }

    @Override
    public Map<String, Integer> findMonthlyAllCompletedCounts(LocalDateTime startDate, LocalDateTime endDate) {
        QOrder order = QOrder.order;

        return jpaQueryFactory
                .select(
                        Expressions.stringTemplate("DATE_FORMAT({0}, '%Y-%m')", order.allCompletedAt),
                        order.count().intValue()
                )
                .from(order)
                .where(order.allCompletedAt.isNotNull()
                        .and(order.allCompletedAt.between(startDate, endDate)))
                .groupBy(Expressions.stringTemplate("DATE_FORMAT({0}, '%Y-%m')", order.allCompletedAt))
                .fetch()
                .stream()
                .collect(Collectors.toMap(
                        tuple -> tuple.get(0, String.class),
                        tuple -> tuple.get(1, Integer.class)
                ));
    }

    @Override
    public Map<String, Map<ERecoveryOption, Integer>> findMonthlyRecoveryOptionCounts(LocalDateTime startDate, LocalDateTime endDate, Boolean isApplied, Boolean isArrived, Boolean isCompleted) {
        QOrder order = QOrder.order;
        QDocument document = QDocument.document;
        
        DateTimePath<LocalDateTime> timePath = order.createdAt;
        if (Boolean.TRUE.equals(isCompleted)) {
            timePath = order.allCompletedAt;
        } else if (Boolean.TRUE.equals(isArrived)) {
            timePath = order.arrivedAt;
        } else {
            timePath = order.createdAt; // APPLY 기준
        }

        BooleanExpression predicate = timePath.isNotNull().and(timePath.between(startDate, endDate));
        
        return jpaQueryFactory
                .select(
                        Expressions.stringTemplate("DATE_FORMAT({0}, '%Y-%m')", timePath),
                        document.recoveryOption,
                        document.count().intValue()
                )
                .from(order)
                .join(order.documents, document)
                .where(predicate)
                .groupBy(
                        Expressions.stringTemplate("DATE_FORMAT({0}, '%Y-%m')", timePath),
                        document.recoveryOption
                )
                .fetch()
                .stream()
                .collect(Collectors.groupingBy(
                        tuple -> tuple.get(0, String.class),
                        Collectors.toMap(
                                tuple -> tuple.get(1, ERecoveryOption.class),
                                tuple -> tuple.get(2, Integer.class)
                        )
                ));
    }

    @Override
    public Map<String, Map<ERecoveryOption, Double>> findMonthlyRecoveryOptionAveragePageCounts(LocalDateTime startDate, LocalDateTime endDate, Boolean isApplied, Boolean isArrived, Boolean isCompleted) {
        QOrder order = QOrder.order;
        QDocument document = QDocument.document;
        
        DateTimePath<LocalDateTime> timePath = order.createdAt;
        if (Boolean.TRUE.equals(isCompleted)) {
            timePath = order.allCompletedAt;
        } else if (Boolean.TRUE.equals(isArrived)) {
            timePath = order.arrivedAt;
        } else {
            timePath = order.createdAt; // APPLY 기준
        }

        BooleanExpression predicate = timePath.isNotNull().and(timePath.between(startDate, endDate));
        
        return jpaQueryFactory
                .select(
                        Expressions.stringTemplate("DATE_FORMAT({0}, '%Y-%m')", timePath),
                        document.recoveryOption,
                        document.pageCount.avg()
                )
                .from(order)
                .join(order.documents, document)
                .where(predicate)
                .groupBy(
                        Expressions.stringTemplate("DATE_FORMAT({0}, '%Y-%m')", timePath),
                        document.recoveryOption
                )
                .fetch()
                .stream()
                .collect(Collectors.groupingBy(
                        tuple -> tuple.get(0, String.class),
                        Collectors.toMap(
                                tuple -> tuple.get(1, ERecoveryOption.class),
                                tuple -> Optional.ofNullable(tuple.get(2, Double.class)).orElse(0.0)
                        )
                ));
    }

    @Override
    public Map<String, Map<ERecoveryOption, Double>> findMonthlyRecoveryOptionAverageDocumentPrices(LocalDateTime startDate, LocalDateTime endDate, Boolean isApplied, Boolean isArrived, Boolean isCompleted) {
        QOrder order = QOrder.order;
        QDocument document = QDocument.document;
        
        DateTimePath<LocalDateTime> timePath = order.createdAt;
        if (Boolean.TRUE.equals(isCompleted)) {
            timePath = order.allCompletedAt;
        } else if (Boolean.TRUE.equals(isArrived)) {
            timePath = order.arrivedAt;
        } else {
            timePath = order.createdAt; // APPLY 기준
        }

        BooleanExpression predicate = timePath.isNotNull().and(timePath.between(startDate, endDate));
        
        return jpaQueryFactory
                .select(
                        Expressions.stringTemplate("DATE_FORMAT({0}, '%Y-%m')", timePath),
                        document.recoveryOption,
                        document.totalAmount.avg()
                )
                .from(order)
                .join(order.documents, document)
                .where(predicate)
                .groupBy(
                        Expressions.stringTemplate("DATE_FORMAT({0}, '%Y-%m')", timePath),
                        document.recoveryOption
                )
                .fetch()
                .stream()
                .collect(Collectors.groupingBy(
                        tuple -> tuple.get(0, String.class),
                        Collectors.toMap(
                                tuple -> tuple.get(1, ERecoveryOption.class),
                                tuple -> Optional.ofNullable(tuple.get(2, Double.class)).orElse(0.0)
                        )
                ));
    }

    @Override
    public Order findByIdWithDocumentsOrElseThrow(Long id) {
        return orderJpaRepository.findByIdWithDocuments(id)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_ORDER, "주문 ID: " + id));
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
            predicate = predicate.and(
                    order.createdAt.loe(LocalDate.parse(endDate).atStartOfDay().plusDays(1).minusNanos(1)));
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
            case "document-name" -> predicate.and(order.documents.any().name.containsIgnoreCase(search));
            case "tracking-number" -> predicate.and(order.delivery.trackingNumber.containsIgnoreCase(search));
            case "email" -> predicate.and(order.user.email.containsIgnoreCase(search)
                    .or(order.delivery.email.containsIgnoreCase(search)));
            case "phone-number" -> predicate.and(order.user.phoneNumber.containsIgnoreCase(search)
                    .or(order.delivery.phoneNumber.containsIgnoreCase(search)));
            case "memo" -> predicate.and(order.memo.containsIgnoreCase(search));
            case "address" -> predicate.and(order.delivery.address.addressName.containsIgnoreCase(search)
                    .or(order.delivery.address.region1DepthName.containsIgnoreCase(search))
                    .or(order.delivery.address.region2DepthName.containsIgnoreCase(search))
                    .or(order.delivery.address.region3DepthName.containsIgnoreCase(search))
                    .or(order.delivery.address.region4DepthName.containsIgnoreCase(search))
                    .or(order.delivery.address.addressDetail.containsIgnoreCase(search)));
            default -> predicate;
        };
    }

    private OrderSpecifier<?> resolveSort(QOrder order, String sort, Direction direction) {
        if (direction.isAscending()) {
            return switch (sort.toLowerCase()) {
                case "order-date" -> order.createdAt.asc();
                case "payment-date" -> order.payment.createdAt.asc();
                case "document-count" -> order.documents.size().asc();
                case "total-amount" -> order.totalAmount.asc();
                case "payment-amount" -> order.payment.totalAmount.asc();
                case "pdf-send-date" -> order.pdfSendDate.asc();
                default -> order.id.asc();
            };
        } else {
            return switch (sort.toLowerCase()) {
                case "order-date" -> order.createdAt.desc();
                case "payment-date" -> order.payment.createdAt.desc();
                case "document-count" -> order.documents.size().desc();
                case "total-amount" -> order.totalAmount.desc();
                case "payment-amount" -> order.payment.totalAmount.desc();
                case "pdf-send-date" -> order.pdfSendDate.desc();
                default -> order.id.desc();
            };
        }
    }


    private BooleanExpression buildFilterPredicate(QOrder order, Boolean isApplied, Boolean isArrived, Boolean isCompleted) {
        BooleanExpression predicate = Expressions.TRUE;
        
        if (isApplied != null && isApplied) {
            predicate = predicate.and(order.orderStatus.isNotNull())
                                .and(order.orderStatus.ne(EOrderStatus.CANCEL));
        }
        
        if (isArrived != null && isArrived) {
            predicate = predicate.and(order.arrivedAt.isNotNull());
        }
        
        if (isCompleted != null && isCompleted) {
            predicate = predicate.and(order.orderStatus.eq(EOrderStatus.ALL_COMPLETED));
        }
        
        return predicate;
    }

    @Override
    public Map<EOrderStatus, Long> findOrderStatusCountsByIsInProgress(Boolean isInProgress) {
        QOrder order = QOrder.order;
        
        BooleanExpression predicate = Expressions.TRUE;
        
        if (isInProgress != null) {
            if (isInProgress) {
                // 진행 중인 상태(취소·완료 제외)
                predicate = predicate.and(
                        order.orderStatus.notIn(EOrderStatus.CANCEL, EOrderStatus.ALL_COMPLETED)
                );
            } else {
                // 진행 중이 아닌 상태(취소 혹은 전부 완료)
                predicate = predicate.and(
                        order.orderStatus.in(EOrderStatus.CANCEL, EOrderStatus.ALL_COMPLETED)
                );
            }
        }
        
        List<Tuple> results = jpaQueryFactory
                .select(order.orderStatus, order.count())
                .from(order)
                .where(predicate)
                .groupBy(order.orderStatus)
                .fetch();
        
        return results.stream()
                .collect(Collectors.toMap(
                        tuple -> tuple.get(order.orderStatus),
                        tuple -> tuple.get(order.count())
                ));
    }
}
