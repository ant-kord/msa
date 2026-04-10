package com.example.payment.integration.order.config;

import com.example.payment.integration.order.config.properties.RabbitMqOrderServiceProperties;
import com.example.payment.integration.order.dto.request.PaymentRequestMessage;
import com.example.payment.integration.order.dto.response.PaymentResponseMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.DefaultClassMapper;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * Конфигурационный класс для настройки RabbitMQ для сервиса оплаты.
 */
@Configuration
@RequiredArgsConstructor
public class RabbitMqOrderServiceConfig {


    private final RabbitMqOrderServiceProperties properties;

    /**
     * Создает очередь для ответа об оплате заказа.
     *
     * @return очередь
     */
    @Bean
    public Queue paymentResponseQueue() {
        return QueueBuilder.durable(properties.queueResponseName())
                .build();
    }

    /**
     * Создает прямой обменник для ответа об оплате заказа.
     *
     * @return обменник
     */
    @Bean
    public DirectExchange paymentResponseExchange() {
        return new DirectExchange(properties.exchangeResponseName());
    }

    /**
     * Создает связывание между очередью и обменником.
     *
     * @param paymentResponseQueue    очередь
     * @param paymentResponseExchange обменник
     * @return связывание
     */
    @Bean
    public Binding queueBinding(Queue paymentResponseQueue,
                                DirectExchange paymentResponseExchange) {
        return BindingBuilder
                .bind(paymentResponseQueue)
                .to(paymentResponseExchange)
                .with(properties.queueResponseName());
    }

    /**
     * Настраивает шаблон RabbitTemplate с JSON-конвертером.
     *
     * @param connectionFactory фабрика соединений
     * @return настроенный RabbitTemplate
     */
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        final RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jsonConverter());
        return rabbitTemplate;
    }

    /**
     * Создает фабрику слушателей с JSON-конвертером.
     *
     * @param connectionFactory фабрика соединений
     * @return фабрика слушателей
     */
    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(jsonConverter());
        factory.setDefaultRequeueRejected(false);
        factory.setAutoStartup(true);
        return factory;
    }

    /**
     * Создает JSON-конвертер сообщений с маппингом классов.
     *
     * @return JSON-конвертер
     */
    @Bean
    public JacksonJsonMessageConverter jsonConverter() {
        JacksonJsonMessageConverter converter = new JacksonJsonMessageConverter();
        converter.setClassMapper(classMapper());
        converter.getJavaTypeMapper().addTrustedPackages("com.example", "java");
        return converter;
    }

    /**
     * Создает маппер классов для JSON-конвертера.
     *
     * @return маппер классов
     */
    @Bean
    public DefaultClassMapper classMapper() {
        DefaultClassMapper classMapper = new DefaultClassMapper();
        Map<String, Class<?>> idClassMapping = new HashMap<>();

        idClassMapping.put("payment-request", PaymentRequestMessage.class);
        idClassMapping.put("payment-response", PaymentResponseMessage.class);

        classMapper.setIdClassMapping(idClassMapping);
        return classMapper;
    }

}
