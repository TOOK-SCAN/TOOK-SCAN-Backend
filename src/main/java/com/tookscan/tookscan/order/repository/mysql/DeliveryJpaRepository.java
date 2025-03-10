package com.tookscan.tookscan.order.repository.mysql;

import com.tookscan.tookscan.order.domain.Delivery;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DeliveryJpaRepository extends JpaRepository<Delivery, Long> {

    @EntityGraph(attributePaths = {"order"})
    @Query("SELECT d FROM Delivery d WHERE d.id = :id")
    Optional<Delivery> findByIdWithOrder(@Param("id") Long id);
}
