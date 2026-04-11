package com.example.payment.repository;

import com.example.payment.entity.IdempotencyKey;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.lang.NonNull;

import java.util.Optional;

public interface IdempotencyRepository extends JpaRepository<IdempotencyKey, String> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @NonNull
    Optional<IdempotencyKey> findById(@NonNull String key);
}
