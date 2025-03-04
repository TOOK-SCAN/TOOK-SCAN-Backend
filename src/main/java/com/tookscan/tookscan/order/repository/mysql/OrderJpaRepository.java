package com.tookscan.tookscan.order.repository.mysql;

import com.tookscan.tookscan.account.domain.User;
import com.tookscan.tookscan.order.domain.Order;
import com.tookscan.tookscan.order.domain.type.EOrderStatus;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
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
            "JOIN o.documents d " +
            "WHERE o.user = :user " +
            "AND (o.orderNumber LIKE %:search% " +
            "OR d.name LIKE %:search%)")
    Page<Order> findAllByUserAndSearch(@Param("user") User user, @Param("search") String search, Pageable pageable);

    @Query("SELECT o FROM Order o " +
            "WHERE o.user = :user")
    Page<Order> findAllByUser(User user, Pageable pageable);

    Optional<Order> findByOrderNumber(String orderNumber);

    @Query("SELECT DISTINCT o FROM Order o " +
            "JOIN FETCH o.documents d " +
            "WHERE o.user.id IN :userIds")
    List<Order> findAllByUserIds(@Param("userIds") List<UUID> userIds);

    @Query("SELECT DISTINCT o FROM Order o " +
            "LEFT JOIN FETCH o.documents d " +
            "LEFT JOIN FETCH d.pricePolicy p " +
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
            "LEFT JOIN FETCH d.pricePolicy p " +
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
  
    @Query("SELECT o.id FROM Order o WHERE o.createdAt < :dateTime")
    List<Long> findIdsByCreatedAtBefore(@Param("dateTime") LocalDateTime dateTime);

    Integer countByCreatedAtBetween(LocalDateTime createdAt, LocalDateTime createdAt2);

    @EntityGraph(attributePaths = {"documents", "documents.pricePolicy", "delivery"})
    @Query("SELECT o FROM Order o WHERE o.id = :id")
    Optional<Order> findByIdWithDocuments(@Param("id") Long id);
}
