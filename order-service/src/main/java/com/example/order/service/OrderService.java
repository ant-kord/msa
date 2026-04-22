package com.example.order.service;

import com.example.order.entity.Order;
import com.example.order.enums.OrderStatus;
import com.example.order.dto.request.OrderRequest;
import com.example.order.integration.payment.dto.response.PaymentResponseMessage;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


public interface OrderService {

    Order createOrder(OrderRequest request);

    //Order createOrderOld(OrderRequest request);

    void changePaymentStatus(PaymentResponseMessage response);

    void changeOrderStatus(UUID orderId, OrderStatus status);

    void cancelOrder(UUID orderId);

    Optional<Order> getOrder(String id);

    List<Order> listOrders();

    Optional<Order> updateOrder(String id, OrderRequest request, OrderStatus status);

    boolean deleteOrder(String id);




}
