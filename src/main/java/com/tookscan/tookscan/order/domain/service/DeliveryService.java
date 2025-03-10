package com.tookscan.tookscan.order.domain.service;

import com.tookscan.tookscan.address.domain.Address;
import com.tookscan.tookscan.order.domain.Delivery;
import com.tookscan.tookscan.order.domain.type.EDeliveryStatus;
import org.springframework.stereotype.Service;

@Service
public class DeliveryService {
    public Delivery createDelivery(
            String receiverName,
            String phoneNumber,
            String email,
            EDeliveryStatus deliveryStatus,
            String request,
            Address address,
            Integer deliveryPrice
    ) {
        return Delivery.builder()
                .receiverName(receiverName)
                .phoneNumber(phoneNumber)
                .email(email)
                .request(request)
                .deliveryStatus(deliveryStatus)
                .address(address)
                .deliveryPrice(deliveryPrice)
                .build();
    }

    public void updateSelf(
            Delivery delivery,
            String receiverName,
            String phoneNumber,
            Address address,
            String request,
            String trackingNumber,
            String email
    ) {
        delivery.updateReceiverName(receiverName);
        delivery.updatePhoneNumber(phoneNumber);
        delivery.updateEmail(email);
        delivery.updateRequest(request);
        delivery.updateTrackingNumber(trackingNumber);
        delivery.updateAddress(address);

    }
}
