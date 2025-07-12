package com.tookscan.tookscan.account.repository.impl;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.tookscan.tookscan.account.domain.QUser;
import com.tookscan.tookscan.account.domain.QUserGroup;
import com.tookscan.tookscan.account.domain.User;
import com.tookscan.tookscan.account.repository.UserRepository;
import com.tookscan.tookscan.account.repository.mysql.UserJpaRepository;
import com.tookscan.tookscan.core.exception.error.ErrorCode;
import com.tookscan.tookscan.core.exception.type.CommonException;
import com.tookscan.tookscan.order.domain.QOrder;
import com.tookscan.tookscan.order.domain.QDocument;
import com.tookscan.tookscan.security.domain.type.ESecurityProvider;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {

    private final UserJpaRepository userJpaRepository;
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public User findByIdOrElseThrow(UUID userId) {
        return userJpaRepository.findById(userId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_ACCOUNT));
    }

    @Override
    public List<User> findByIds(List<UUID> userIds) {
        return userJpaRepository.findUserByIds(userIds);
    }

    @Override
    public User save(User user) {
        return userJpaRepository.save(user);
    }

    @Override
    public User findByPhoneNumberAndNameOrElseThrow(String phoneNumber, String name) {
        return userJpaRepository.findByPhoneNumberAndName(phoneNumber, name)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_ACCOUNT));
    }

    @Override
    public List<User> findByIdsWithDetails(List<UUID> userIds) {
        return userJpaRepository.findUserByIdsWithDetails(userIds);
    }

    @Override
    public Integer countByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate) {
        return userJpaRepository.countByCreatedAtBetween(startDate, endDate);
    }

    @Override
    public Page<UUID> findUserIdsByFilters(String searchType, String search, Long groupId, ESecurityProvider provider, LocalDate startDate, LocalDate endDate, Pageable pageable, String status, String sort, Direction direction) {
        QUser user = QUser.user;
        QUserGroup userGroup = QUserGroup.userGroup;

        BooleanExpression predicate = getSearchPredicate(user, searchType, search);
        if (predicate == null) {
            predicate = Expressions.asBoolean(true).isTrue();
        }

        if (startDate != null) {
            predicate = predicate.and(user.createdAt.goe(startDate.atStartOfDay()));
        }

        if (endDate != null) {
            predicate = predicate.and(user.createdAt.lt(endDate.plusDays(1).atStartOfDay()));
        }

        if (groupId != null) {
            predicate = predicate.and(user.userGroups.any().id.eq(groupId));
        }

        if (provider != null) {
            predicate = predicate.and(user.provider.eq(provider));
        }

        if (status != null && (status.equals("enrolled") || !status.equals("withdrew"))) {
            if( status.equals("enrolled")) {
                predicate = predicate.and(user.deletedAt.isNull());
            } else {
                predicate = predicate.and(user.deletedAt.isNotNull());
            }
        }

        // 정렬이 주문 관련인 경우 복잡한 쿼리 사용
        if (isOrderRelatedSort(sort)) {
            return findUserIdsByFiltersWithOrderSort(predicate, pageable, sort, direction);
        }

        // 기본 정렬 (사용자 정보 기준)
        var query = jpaQueryFactory
                .select(user.id)
                .from(user)
                .leftJoin(user.userGroups, userGroup)
                .where(predicate);

        // 기본 정렬 적용
        if (sort != null && direction != null) {
            query = query.orderBy(resolveUserSort(user, sort, direction));
        } else {
            query = query.orderBy(user.createdAt.desc());
        }

        List<UUID> userIds = query
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = jpaQueryFactory
                .select(user.count())
                .from(user)
                .leftJoin(user.userGroups, userGroup)
                .where(predicate)
                .fetchOne();

        total = (total == null) ? 0L : total;

        return new PageImpl<>(userIds, pageable, total);
    }

    private boolean isOrderRelatedSort(String sort) {
        return sort != null && (sort.equals("order-count") || sort.equals("total-pages") || sort.equals("total-amount"));
    }

    private Page<UUID> findUserIdsByFiltersWithOrderSort(BooleanExpression predicate, Pageable pageable, String sort, Direction direction) {
        QUser user = QUser.user;
        QUserGroup userGroup = QUserGroup.userGroup;
        QOrder order = QOrder.order;
        QDocument document = QDocument.document;

        var query = jpaQueryFactory
                .select(user.id)
                .from(user)
                .leftJoin(user.userGroups, userGroup)
                .leftJoin(user.orders, order)
                .leftJoin(order.documents, document)
                .where(predicate)
                .groupBy(user.id);

        // 주문 관련 정렬 적용
        query = query.orderBy(resolveOrderSort(user, order, document, sort, direction));

        List<UUID> userIds = query
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // 전체 개수 조회 (정렬 없이)
        Long total = jpaQueryFactory
                .select(user.countDistinct())
                .from(user)
                .leftJoin(user.userGroups, userGroup)
                .where(predicate)
                .fetchOne();

        total = (total == null) ? 0L : total;

        return new PageImpl<>(userIds, pageable, total);
    }

    private OrderSpecifier<?> resolveUserSort(QUser user, String sort, Direction direction) {
        if (direction.isAscending()) {
            return switch (sort.toLowerCase()) {
                case "created-at" -> user.createdAt.asc();
                case "name" -> user.name.asc();
                case "email" -> user.email.asc();
                case "phone-number" -> user.phoneNumber.asc();
                default -> user.createdAt.asc();
            };
        } else {
            return switch (sort.toLowerCase()) {
                case "created-at" -> user.createdAt.desc();
                case "name" -> user.name.desc();
                case "email" -> user.email.desc();
                case "phone-number" -> user.phoneNumber.desc();
                default -> user.createdAt.desc();
            };
        }
    }

    private OrderSpecifier<?> resolveOrderSort(QUser user, QOrder order, QDocument document, String sort, Direction direction) {
        if (direction.isAscending()) {
            return switch (sort.toLowerCase()) {
                case "order-count" -> order.count().asc();
                case "total-pages" -> document.pageCount.sum().asc();
//                case "total-amount" -> document.additionalPrice.sum().asc(); // TODO: 정렬 기준이 잘못 잡혀있음
                default -> user.createdAt.desc();
            };
        } else {
            return switch (sort.toLowerCase()) {
                case "order-count" -> order.count().desc();
                case "total-pages" -> document.pageCount.sum().desc();
//                case "total-amount" -> document.additionalPrice.sum().desc() // TODO: 정렬 기준이 잘못잡혀있음
                default -> user.createdAt.desc();
            };
        }
    }

    private BooleanExpression getSearchPredicate(QUser user, String filterColumn, String search) {

        if (filterColumn == null || search == null || search.isBlank()) {
            return null;
        }

        // 동적 조건 생성 함수 매핑
        Map<String, Function<String, BooleanExpression>> predicates = Map.of(
                "email", user.email::containsIgnoreCase,
                "name", user.name::containsIgnoreCase,
                "phone-number", user.phoneNumber::containsIgnoreCase,
                "serial-id", user.serialId::containsIgnoreCase,
                "memo", user.memo::containsIgnoreCase
        );

        // 검색 타입에 따른 Predicate 생성
        Function<String, BooleanExpression> predicateFunction = predicates.get(filterColumn);

        if (predicateFunction == null) {
            throw new CommonException(ErrorCode.INVALID_ENUM_TYPE);
        }

        return predicateFunction.apply(search);
    }
}
