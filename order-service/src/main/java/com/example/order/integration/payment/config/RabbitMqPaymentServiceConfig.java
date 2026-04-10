package com.example.order.integration.payment.config;

import com.example.order.integration.payment.config.properties.RabbitMqPaymentServiceProperties;
import com.example.order.integration.payment.dto.request.PaymentRequestMessage;
import com.example.order.integration.payment.dto.response.PaymentResponseMessage;
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
public class RabbitMqPaymentServiceConfig {


    private final RabbitMqPaymentServiceProperties properties;

    /**
     * Создает очередь для ответа об оплате заказа.
     *
     * @return очередь
     */
    @Bean
    public Queue paymentRequestQueue() {
        return QueueBuilder.durable(properties.queueRequestName())
                .build();
    }

    /**
     * Создает прямой обменник для ответа об оплате заказа.
     *
     * @return обменник
     */
    @Bean
    public DirectExchange paymentRequestExchange() {

        return new DirectExchange(properties.exchangeRequestName());
    }

    /**
     * Создает связывание между очередью и обменником.
     *
     * @param paymentRequestQueue    очередь
     * @param paymentRequestExchange обменник
     * @return связывание
     */
    @Bean
    public Binding queueBinding(Queue paymentRequestQueue,
                                DirectExchange paymentRequestExchange) {
        return BindingBuilder
                .bind(paymentRequestQueue)
                .to(paymentRequestExchange)
                .with(properties.queueRequestName());
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
