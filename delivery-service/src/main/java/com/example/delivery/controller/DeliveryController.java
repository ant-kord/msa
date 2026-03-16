package com.example.delivery.controller;

import com.example.delivery.domain.Delivery;
import com.example.delivery.dto.AddressDTO;
import com.example.delivery.dto.DeliveryRequest;
import com.example.delivery.dto.DeliveryResponse;
import com.example.delivery.service.DeliveryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/deliveries")
public class DeliveryController {

    private final DeliveryService svc;

    public DeliveryController(DeliveryService svc) {
        this.svc = svc;
    }

    @PostMapping
    public ResponseEntity<DeliveryResponse> create(@RequestBody DeliveryRequest req) {
        try {
            Delivery d = svc.createDelivery(req);
            return new ResponseEntity<>(toResponse(d), HttpStatus.CREATED);
        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage());
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to create delivery");
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<DeliveryResponse> get(@PathVariable String id) {
        return svc.getDelivery(id)
                .map(d -> ResponseEntity.ok(toResponse(d)))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Delivery not found"));
    }

    @GetMapping
    public ResponseEntity<List<DeliveryResponse>> list() {
        return ResponseEntity.ok(svc.listDeliveries().stream().map(this::toResponse).collect(Collectors.toList()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<DeliveryResponse> update(@PathVariable String id, @RequestBody DeliveryRequest req) {
        try {
            return svc.updateDelivery(id, req)
                    .map(d -> ResponseEntity.ok(toResponse(d)))
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Delivery not found"));
        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        boolean deleted = svc.deleteDelivery(id);
        if (deleted) return ResponseEntity.noContent().build();
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Delivery not found");
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
