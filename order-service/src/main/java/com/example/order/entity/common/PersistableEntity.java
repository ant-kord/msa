package com.example.order.entity.common;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import org.hibernate.annotations.ColumnDefault;
import org.springframework.data.domain.Persistable;

import java.time.OffsetDateTime;

@MappedSuperclass
public abstract class PersistableEntity<T> implements Persistable<T> {

    @Column(name = "created_at", insertable = false, updatable = false)
    @ColumnDefault("now()")
    private OffsetDateTime createdAt;

    @Override
    public boolean isNew() {
        return createdAt == null;
    }
}
