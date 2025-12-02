package com.whisperline.backend.repository;

import com.whisperline.backend.entity.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    List<ChatMessage> findByUserId(String userId);
    List<ChatMessage> findByUserIdOrderByTimestampDesc(String userId);
    long countByUserId(String userId);
    
    @Query("SELECT cm.userId, AVG(cm.riskLevel) as avgRisk, COUNT(cm) as msgCount " +
           "FROM ChatMessage cm WHERE cm.riskLevel IS NOT NULL " +
           "GROUP BY cm.userId " +
           "ORDER BY avgRisk DESC")
    List<Object[]> findAverageRiskLevelByUser();
}

