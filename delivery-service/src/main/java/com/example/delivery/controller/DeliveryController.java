package com.example.delivery.controller;

import com.example.delivery.controller.doc.DeliveryControllerDoc;
import com.example.delivery.domain.Delivery;
import com.example.delivery.dto.AddressDTO;
import com.example.delivery.dto.DeliveryRequest;
import com.example.delivery.dto.DeliveryResponse;
import com.example.delivery.service.DeliveryService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/deliveries")
@RequiredArgsConstructor
public class DeliveryController implements DeliveryControllerDoc {

    private final DeliveryService svc;

    @Override
    @PostMapping
    @CircuitBreaker(name = "deliveryService", fallbackMethod = "createDeliveryFallback")
    public ResponseEntity<DeliveryResponse> createDelivery(@RequestBody DeliveryRequest request) {
        try {
            Delivery d = svc.createDelivery(request);
            return new ResponseEntity<>(toResponse(d), HttpStatus.CREATED);
        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage());
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to create delivery");
        }
    }

    @Override
    @GetMapping("/{id}")
    public ResponseEntity<DeliveryResponse> getDelivery(@PathVariable String id) {
        return svc.getDelivery(id)
                .map(d -> ResponseEntity.ok(toResponse(d)))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Delivery not found"));
    }

    @Override
    @GetMapping
    public ResponseEntity<List<DeliveryResponse>> listDelivers() {
        return ResponseEntity.ok(svc.listDeliveries().stream().map(this::toResponse).collect(Collectors.toList()));
    }

    @Override
    @PutMapping("/{id}")
    public ResponseEntity<DeliveryResponse> updateDelivery(@PathVariable String id, @RequestBody DeliveryRequest request) {
        try {
            return svc.updateDelivery(id, request)
                    .map(d -> ResponseEntity.ok(toResponse(d)))
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Delivery not found"));
        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage());
        }
    }

    @Override
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDelivery(@PathVariable String id) {
        boolean deleted = svc.deleteDelivery(id);
        if (deleted) return ResponseEntity.noContent().build();
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Delivery not found");
    }

    public ResponseEntity<DeliveryResponse> createDeliveryFallback(DeliveryRequest request, Throwable t) {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
    }

    private DeliveryResponse toResponse(Delivery d) {
        AddressDTO addr = null;
        if (d.getAddress() != null) {
            addr = AddressDTO.builder()
                    .street(d.getAddress().getStreet())
                    .city(d.getAddress().getCity())
                    .postalCode(d.getAddress().getPostalCode())
                    .country(d.getAddress().getCountry())
                    .build();
        }
        return DeliveryResponse.builder()
                .id(d.getId())
                .orderId(d.getOrderId())
                .address(addr)
                .status(d.getStatus())
                .createdAt(d.getCreatedAt())
                .deliveredAt(d.getDeliveredAt())
                .build();
    }
}
