package com.tookscan.tookscan.order.repository;

import com.tookscan.tookscan.order.domain.Delivery;

public interface DeliveryRepository {
    void save(Delivery delivery);

    Delivery findByIdOrElseThrow(Long id);

    Delivery findByIdWithOrderOrElseThrow(Long id);
}
