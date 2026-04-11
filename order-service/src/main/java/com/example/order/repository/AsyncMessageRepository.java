package com.example.order.repository;

import com.example.order.entity.AsyncMessage;
import com.example.order.entity.AsyncMessageId;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface AsyncMessageRepository extends JpaRepository<AsyncMessage, AsyncMessageId> {

    @Query("SELECT m FROM AsyncMessage m WHERE m.status = 'CREATED' AND m.type = 'OUTBOX' ORDER BY m.createdAt")
    List<AsyncMessage> findUnsentOutboxMessages(Pageable pageable);
}
