package com.example.payment.entity;

import com.example.payment.enums.IdempotencyKeyStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.ZonedDateTime;

@Entity
@Table(name = "idempotency_keys")
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "key")
@ToString
@Getter
@Setter
public class IdempotencyKey {

    @Id
    @Column(name = "key_value")
    private String key;

    @Enumerated(EnumType.STRING)
    private IdempotencyKeyStatus status;

    @Lob
    private String response;

    private ZonedDateTime createdAt;

    private int statusCode;

    public IdempotencyKey(String key, IdempotencyKeyStatus status) {
        this.key = key;
        this.status = status;
        this.createdAt = ZonedDateTime.now();
    }
}
