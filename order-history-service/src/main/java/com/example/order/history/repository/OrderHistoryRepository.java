package com.example.order.history.repository;

import com.example.order.history.entity.OrderHistory;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface OrderHistoryRepository extends MongoRepository<OrderHistory, String> {
}
