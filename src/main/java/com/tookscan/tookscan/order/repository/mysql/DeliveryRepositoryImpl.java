package com.tookscan.tookscan.order.repository.mysql;

import com.tookscan.tookscan.core.exception.error.ErrorCode;
import com.tookscan.tookscan.core.exception.type.CommonException;
import com.tookscan.tookscan.order.domain.Delivery;
import com.tookscan.tookscan.order.repository.DeliveryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class DeliveryRepositoryImpl implements DeliveryRepository {

    private final DeliveryJpaRepository deliveryJpaRepository;


    @Override
    public void save(Delivery delivery) {
        deliveryJpaRepository.save(delivery);
    }

    @Override
    public Delivery findByIdOrElseThrow(Long id) {
        return deliveryJpaRepository.findById(id)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_DELIVERY, "배송 ID: " + id));
    }
}
