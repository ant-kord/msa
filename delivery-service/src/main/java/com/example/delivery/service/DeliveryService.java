package com.example.delivery.service;

import com.example.delivery.entity.Delivery;
import com.example.delivery.dto.request.DeliveryRequest;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


public interface DeliveryService {

    Delivery createDelivery(DeliveryRequest req);

    Delivery createDelivery(UUID orderId);

    Optional<Delivery> getDelivery(String id);

    List<Delivery> listDeliveries();

    Optional<Delivery> updateDelivery(String id, DeliveryRequest req);

    boolean deleteDelivery(String id);

    void deleteByOrderId(String orderId);

}
