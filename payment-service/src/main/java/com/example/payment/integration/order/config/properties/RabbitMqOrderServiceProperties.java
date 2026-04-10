package com.example.payment.integration.order.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "rabbitmq.service.order")
public record RabbitMqOrderServiceProperties(
        String exchangeResponseName,
        String queueResponseName,
        String queueRequestName
) {
}
