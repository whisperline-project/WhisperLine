package com.whisperline.backend.controller;

import com.whisperline.backend.dto.AuthResponse;
import com.whisperline.backend.dto.LoginRequest;
import com.whisperline.backend.dto.SignupRequest;
import com.whisperline.backend.entity.User;
import com.whisperline.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:3000")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/signup")
    public ResponseEntity<AuthResponse> signup(@RequestBody SignupRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new AuthResponse(false, "Username already exists"));
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new AuthResponse(false, "Email already exists"));
        }

        User user = new User(
                request.getName(),
                request.getUsername(),
                request.getPassword(),
                request.getEmail()
        );

        userRepository.save(user);

        return ResponseEntity.ok(new AuthResponse(
                true,
                "User registered successfully",
                user.getUsername(),
                user.getName()
        ));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElse(null);

        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new AuthResponse(false, "Invalid username or password"));
        }

        if (!user.getPassword().equals(request.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new AuthResponse(false, "Invalid username or password"));
        }

        return ResponseEntity.ok(new AuthResponse(
                true,
                "Login successful",
                user.getUsername(),
                user.getName()
        ));
    }
}

