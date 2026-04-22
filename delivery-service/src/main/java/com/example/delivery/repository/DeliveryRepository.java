package com.example.delivery.repository;

import com.example.delivery.entity.Delivery;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface DeliveryRepository extends JpaRepository<Delivery, String> {

    @Modifying
    @Transactional
    void deleteByOrderId(String orderId);
}
