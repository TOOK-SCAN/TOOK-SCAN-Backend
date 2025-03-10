package com.tookscan.tookscan.order.repository;

import com.tookscan.tookscan.account.domain.User;
import com.tookscan.tookscan.order.domain.Order;
import com.tookscan.tookscan.order.domain.type.EOrderStatus;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;

public interface OrderRepository {

    void save(Order order);

    Order findByIdOrElseThrow(Long id);

    List<Order> findAllByIdOrElseThrow(List<Long> ids);

    List<Order> findAllByOrderStatusDateBetweenOrElseThrow(LocalDateTime startDate, LocalDateTime endDate,
                                                           EOrderStatus orderStatus);

    Map<EOrderStatus, Integer> findOrderStatusCounts();

    Page<Long> findOrderSummaries(String startDate, String endDate, String search, String searchType, String sort,
                                  Direction direction, Pageable pageable);

    Page<Long> findOrderOverviews(String startDate, String endDate,
                                  String search, String searchType, String sort, Direction direction,
                                  Pageable pageable, EOrderStatus orderStatus);

    Page<Long> findDeliveriesSummaries(String startDate, String endDate, String search, String searchType,
                                       EOrderStatus orderStatus,
                                       Pageable pageable);

    void deleteAll(List<Order> orders);

    Page<Order> findAllByUserAndSearchOrElseNull(User user, String search, Pageable pageable);

    Order findByOrderNumberOrElseThrow(String orderNumber);

    List<Order> findAllByUserIds(List<UUID> userIds);

    List<Order> findAllWithDocumentsByIdIn(List<Long> ids);

    List<Order> findAllWithDocumentsAndUserByIdIn(List<Long> ids);

    List<Order> findAllByOrderNumberIn(List<String> orderNumber);
  
    List<Long> findIdsByCreatedAtBefore(LocalDateTime dateTime);

    void deleteAllById(List<Long> ids);

    Integer countByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);

    Order findByIdWithDocumentsOrElseThrow(Long id);

}
