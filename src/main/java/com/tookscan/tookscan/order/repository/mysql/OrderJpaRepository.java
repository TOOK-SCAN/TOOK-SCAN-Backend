package com.tookscan.tookscan.order.repository.mysql;

import com.tookscan.tookscan.account.domain.User;
import com.tookscan.tookscan.order.domain.Order;
import com.tookscan.tookscan.order.domain.type.EOrderStatus;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.tookscan.tookscan.order.domain.type.ERecoveryOption;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderJpaRepository extends JpaRepository<Order, Long> {


    @Query("SELECT o FROM Order o " +
            "WHERE o.user = :user")
    Page<Order> findAllByUser(User user, Pageable pageable);

    Integer countByUserAndOrderStatusIn(User user, List<EOrderStatus> orderStatuses);

    Optional<Order> findByOrderNumber(String orderNumber);

    @EntityGraph(attributePaths = {"user", "delivery"})
    Optional<Order> findWithUserAndDeliveryByOrderNumber(String orderNumber);

    @EntityGraph(attributePaths = {"usedCoupon"})
    Optional<Order> findWithUsedCouponById(Long id);

    @EntityGraph(attributePaths = {"user"})
    Optional<Order> findWithUserById(Long id);

    @Query("SELECT DISTINCT o FROM Order o " +
            "JOIN FETCH o.documents d " +
            "WHERE o.user.id IN :userIds")
    List<Order> findAllByUserIds(@Param("userIds") List<UUID> userIds);

    @Query("SELECT DISTINCT o FROM Order o " +
            "LEFT JOIN FETCH o.documents d " +
            "LEFT JOIN FETCH o.delivery del " +
            "LEFT JOIN FETCH o.user u " +
            "WHERE o.createdAt BETWEEN :startDate AND :endDate " +
            "AND o.orderStatus = :orderStatus")
    List<Order> findAllByOrderStatusDateBetween(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("orderStatus") EOrderStatus orderStatus);

    @Query("SELECT DISTINCT o FROM Order o " +
            "LEFT JOIN FETCH o.documents d " +
            "LEFT JOIN FETCH o.delivery del " +
            "LEFT JOIN FETCH o.user u " +
            "WHERE o.id IN :ids")
    List<Order> findAllWithDocumentsByIdIn(@Param("ids") List<Long> ids);

    @Query("SELECT DISTINCT o FROM Order o " +
            "LEFT JOIN FETCH o.documents d " +
            "LEFT JOIN FETCH o.delivery del " +
            "LEFT JOIN FETCH o.user u " +
            "WHERE o.id IN :ids")
    List<Order> findAllWithDocumentsAndUserByIdIn(@Param("ids") List<Long> ids);

    List<Order> findAllByOrderNumberIn(List<String> orderNumber);

    @Query("SELECT o FROM Order o WHERE o.createdAt < :dateTime AND o.orderStatus = :status")
    List<Order> findAllByCreatedAtBeforeWithEOrderStatus(@Param("dateTime") LocalDateTime dateTime,
                                                         @Param("status") EOrderStatus status);

    Integer countByCreatedAtBetween(LocalDateTime createdAt, LocalDateTime createdAt2);

    @Query(
            "SELECT COUNT(o) FROM Order o " +
            "WHERE o.createdAt BETWEEN :startDate AND :endDate " +
            "AND o.orderStatus = :orderStatus"
    )
    Integer countByCreatedAtBetweenAndOrderStatus(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate, @Param("orderStatus") EOrderStatus orderStatus);

    @Query(
            "SELECT COUNT(d) FROM Order o " +
                    "JOIN o.documents d " +
                    "WHERE o.createdAt BETWEEN :startDate AND :endDate " +
                    "AND d.recoveryOption = :recoveryOption"
    )
    Integer countByCreatedAtBetweenAndRecoveryOption(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate, @Param("recoveryOption") ERecoveryOption recoveryOption);

    @EntityGraph(attributePaths = {"documents", "delivery"})
    @Query("SELECT o FROM Order o WHERE o.id = :id")
    Optional<Order> findByIdWithDocuments(@Param("id") Long id);

    @EntityGraph(attributePaths = {"documents", "delivery"})
    @Query("SELECT o FROM Order o WHERE o.id = :id")
    Optional<Order> findByIdWithDocumentsAndDelivery(@Param("id") Long id);
}
