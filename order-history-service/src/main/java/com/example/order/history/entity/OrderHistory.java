package com.example.order.history.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.util.Date;

@Document(collection = "order-history")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderHistory {

    @Id
    private String id;
    private String customerName;
    private String paymentMethod;
    private Date deliveryCreatedAt;
    private String status;
    private BigDecimal orderAmount;
}
