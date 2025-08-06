package com.tookscan.tookscan.order.repository;

import com.tookscan.tookscan.account.domain.User;
import com.tookscan.tookscan.order.domain.Order;
import com.tookscan.tookscan.order.domain.type.EOrderStatus;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.tookscan.tookscan.order.domain.type.ERecoveryOption;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;

public interface OrderRepository {

    void save(Order order);

    void saveAll(List<Order> orders);

    Order findByIdOrElseThrow(Long id);

    Order findWithUserById(Long id);

    Order findWithUsedCouponByIdOrElseThrow(Long id);

    List<Order> findAllByIdOrElseThrow(List<Long> ids);

    List<Order> findAllByOrderStatusDateBetweenOrElseThrow(LocalDateTime startDate, LocalDateTime endDate,
                                                           EOrderStatus orderStatus);

    Integer countByUserAndOrderStatusIn(User user, List<EOrderStatus> orderStatuses);

    Map<EOrderStatus, Integer> findOrderStatusCounts();

    Page<Long> findOrderSummaries(String startDate, String endDate, String search, String searchType, String sort,
                                  Direction direction, Pageable pageable);

    Page<Long> findOrderOverviews(String startDate, String endDate,
                                  String search, String searchType, String sort, Direction direction,
                                  Pageable pageable, EOrderStatus orderStatus, Boolean isOneDayScan,
                                  Boolean hasRecoveryOption, Boolean isAsInProgress, Boolean isInProgress);

    Page<Long> findDeliveriesSummaries(String startDate, String endDate, String search, String searchType,
                                       EOrderStatus orderStatus,
                                       Pageable pageable);

    void deleteAll(List<Order> orders);

    Page<Order> findAllByUserAndSearchAndOrderStatusInOrElseNull(User user, String search, Pageable pageable,
                                                                 String startDate, String endDate,
                                                                 List<EOrderStatus> orderStatuses, String sort,
                                                                 Direction direction);

    Order findByOrderNumberOrElseThrow(String orderNumber);

    Order findWithUserAndDeliveryByOrderNumberOrElseThrow(String orderNumber);

    List<Order> findAllByUserIds(List<UUID> userIds);

    List<Order> findAllWithDocumentsByIdIn(List<Long> ids);

    List<Order> findAllWithDocumentsAndUserByIdIn(List<Long> ids);

    List<Order> findAllByOrderNumberIn(List<String> orderNumber);

    List<Order> findAllByCreatedAtBeforeWithEOrderStatus(LocalDateTime dateTime, EOrderStatus status);

    void deleteAllById(List<Long> ids);

    Integer countByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);

    Integer countByCreatedAtBetweenAndOrderStatus(LocalDateTime startDate, LocalDateTime endDate, EOrderStatus orderStatus);

    Integer countByCreatedAtBetweenAndRecoveryOption(LocalDateTime startDate, LocalDateTime endDate, ERecoveryOption recoveryOption);

    Map<String, Integer> findMonthlyOrderCounts(LocalDateTime startDate, LocalDateTime endDate);

    Map<String, Map<EOrderStatus, Integer>> findMonthlyOrderStatusCounts(LocalDateTime startDate, LocalDateTime endDate);

    Map<String, Map<ERecoveryOption, Integer>> findMonthlyRecoveryOptionCounts(LocalDateTime startDate, LocalDateTime endDate);

    Order findByIdWithDocumentsOrElseThrow(Long id);

    Order findByIdWithDocumentsAndDeliveryOrElseThrow(Long id);

}
