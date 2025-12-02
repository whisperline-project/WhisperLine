package com.whisperline.backend.repository;

import com.whisperline.backend.entity.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    List<ChatMessage> findByUserId(String userId);
    List<ChatMessage> findByUserIdOrderByTimestampDesc(String userId);
    long countByUserId(String userId);
}

