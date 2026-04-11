package com.example.order.service;

import com.example.order.entity.Order;
import com.example.order.dto.OrderStatus;
import com.example.order.dto.OrderRequest;
import com.example.order.integration.payment.dto.response.PaymentResponseMessage;

import java.util.List;
import java.util.Optional;


public interface OrderService {

    Order createOrder(OrderRequest request);

    Order createOrderOld(OrderRequest request);

    void changePaymentStatus(PaymentResponseMessage response);

    Optional<Order> getOrder(String id);

    List<Order> listOrders();

    Optional<Order> updateOrder(String id, OrderRequest request, OrderStatus status);

    boolean deleteOrder(String id);




}
