package com.example.payment.repository;

import com.example.payment.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, String> {

    @Modifying
    @Transactional
    void deleteByOrderId(String orderId);
}
