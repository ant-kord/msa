package com.example.delivery.service;

import com.example.delivery.domain.Address;
import com.example.delivery.domain.Delivery;
import com.example.delivery.enums.DeliveryStatus;
import com.example.delivery.dto.AddressDTO;
import com.example.delivery.dto.DeliveryRequest;
import com.example.delivery.repository.DeliveryRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.OffsetDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class DeliveryService {

    private final DeliveryRepository repo;

    public DeliveryService(DeliveryRepository repo) {
        this.repo = repo;
    }

    public Delivery createDelivery(DeliveryRequest req) {
        validate(req);

        Delivery d = Delivery.builder()
                .orderId(req.getOrderId())
                .address(mapAddress(req.getAddress()))
                .status(req.getStatus() == null ? DeliveryStatus.PENDING : req.getStatus())
                .build();

        return repo.save(d);
    }

    public Optional<Delivery> getDelivery(String id) {
        if (id == null || id.isBlank()) return Optional.empty();
        return repo.findById(id);
    }

    public List<Delivery> listDeliveries() {
        return repo.findAll();
    }

    public Optional<Delivery> updateDelivery(String id, DeliveryRequest req) {
        if (id == null || id.isBlank()) return Optional.empty();

        return repo.findById(id).map(existing -> {
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
            return repo.save(existing);
        });
    }

    public boolean deleteDelivery(String id) {
        if (id == null || id.isBlank()) return false;
        return repo.findById(id).map(d -> {
            repo.delete(d);
            return true;
        }).orElse(false);
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
