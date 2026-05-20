package com.example.order.history;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;


@SpringBootApplication
@ConfigurationPropertiesScan
public class OrderHistoryServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(OrderHistoryServiceApplication.class, args);
    }
}
