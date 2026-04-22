package com.example.delivery.service.impl;

import com.example.delivery.entity.Address;
import com.example.delivery.entity.Delivery;
import com.example.delivery.dto.AddressDTO;
import com.example.delivery.dto.request.DeliveryRequest;
import com.example.delivery.enums.DeliveryStatus;
import com.example.delivery.integration.order.dto.message.OrderCreationStatus;
import com.example.delivery.integration.order.dto.message.OrderCreationStatusMessage;
import com.example.delivery.repository.DeliveryRepository;
import com.example.delivery.service.DeliveryService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.OffsetDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class DeliveryServiceImpl implements DeliveryService {

    private final DeliveryRepository repository;
    private final KafkaTemplate<String, OrderCreationStatusMessage> kafkaTemplate;

    @Value("${kafka.service.order.order-creation-status-topic}")
    private String orderCreationStatusTopic;

    @Transactional
    @Override
    public Delivery createDelivery(DeliveryRequest req) {
        validate(req);

        Delivery d = Delivery.builder()
                .orderId(req.getOrderId())
                .address(mapAddress(req.getAddress()))
                .status(req.getStatus() == null ? DeliveryStatus.PENDING : req.getStatus())
                .build();

        return repository.save(d);
    }

    @Transactional
    @Override
    public Delivery createDelivery(UUID orderId) {
        log.info("Starting creation of delivery for order ID: {}", orderId);
        var delivery = Delivery.builder()
                .orderId(orderId.toString())
                .status(DeliveryStatus.CREATED)
                .build();
        repository.save(delivery);
        log.info("Delivery created with ID: {}", delivery.getId());

        sendStatusMessage(orderId);

        return delivery;
    }


    @Override
    public Optional<Delivery> getDelivery(String id) {
        if (id == null || id.isBlank()) return Optional.empty();
        return repository.findById(id);
    }

    @Override
    public List<Delivery> listDeliveries() {
        return repository.findAll();
    }

    @Override
    public Optional<Delivery> updateDelivery(String id, DeliveryRequest req) {
        if (id == null || id.isBlank()) return Optional.empty();

        return repository.findById(id).map(existing -> {
            if (req.getOrderId() != null && !req.getOrderId().isBlank())
                existing.setOrderId(req.getOrderId());
            if (req.getAddress() != null) existing.setAddress(mapAddress(req.getAddress()));
            if (req.getStatus() != null) {
                if (req.getStatus() == DeliveryStatus.DELIVERED && req.getDeliveredAt() == null) {
                    throw new IllegalArgumentException("deliveredAt must be set when status is DELIVERED");
                }
                existing.setStatus(req.getStatus());
            }
            if (req.getDeliveredAt() != null) {
                try {
                    existing.setDeliveredAt(OffsetDateTime.parse(req.getDeliveredAt()));
                } catch (DateTimeParseException ex) {
                    throw new IllegalArgumentException("deliveredAt must be in ISO_OFFSET_DATE_TIME format");
                }
            }
            return repository.save(existing);
        });
    }

    @Override
    public boolean deleteDelivery(String id) {
        if (id == null || id.isBlank()) return false;
        return repository.findById(id).map(d -> {
            repository.delete(d);
            return true;
        }).orElse(false);
    }

    @Override
    public void deleteByOrderId(String orderId) {
        repository.deleteByOrderId(orderId);
    }

    private void sendStatusMessage(UUID orderId) {
        var statusMessage = OrderCreationStatusMessage.builder()
                .orderId(orderId)
                .status(OrderCreationStatus.DELIVERY_CREATED)
                .build();

        kafkaTemplate.send(orderCreationStatusTopic, statusMessage);
        log.info("Sent delivery creation message to Kafka for orderId ID: {}", orderId);
    }




    private void validate(DeliveryRequest req) {
        if (req == null) throw new IllegalArgumentException("DeliveryRequest cannot be null");
        if (!StringUtils.hasText(req.getOrderId())) throw new IllegalArgumentException("orderId is required");
        if (req.getAddress() == null) throw new IllegalArgumentException("address is required");
    }

    private Address mapAddress(AddressDTO dto) {
        if (dto == null) return null;
        return Address.builder()
                .street(dto.getStreet())
                .city(dto.getCity())
                .postalCode(dto.getPostalCode())
                .country(dto.getCountry())
                .build();
    }
}
