package com.whisperline.backend.controller;

import com.whisperline.backend.dto.UserRiskStats;
import com.whisperline.backend.entity.User;
import com.whisperline.backend.repository.ChatMessageRepository;
import com.whisperline.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "http://localhost:3000")
public class AdminController {

    @Autowired
    private ChatMessageRepository chatMessageRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/dashboard/top-risk-users")
    public ResponseEntity<List<UserRiskStats>> getTopRiskUsers() {
        List<Object[]> results = chatMessageRepository.findAverageRiskLevelByUser();
        
        List<UserRiskStats> userRiskStats = new ArrayList<>();
        
        for (Object[] result : results) {
            String username = (String) result[0];
            Double avgRisk = ((Number) result[1]).doubleValue();
            Long msgCount = ((Number) result[2]).longValue();
            
            User user = userRepository.findByUsername(username).orElse(null);
            if (user != null) {
                userRiskStats.add(new UserRiskStats(
                    username,
                    user.getName(),
                    avgRisk,
                    msgCount
                ));
            }
        }
        
        // Sort by average risk level descending and take top 10
        List<UserRiskStats> top10 = userRiskStats.stream()
            .sorted((a, b) -> Double.compare(b.getAverageRiskLevel(), a.getAverageRiskLevel()))
            .limit(10)
            .collect(Collectors.toList());
        
        return ResponseEntity.ok(top10);
    }
}

