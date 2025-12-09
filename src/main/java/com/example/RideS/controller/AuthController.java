package com.example.RideS.controller;

import com.example.RideS.dto.LoginRequest;
import com.example.RideS.dto.RegisterRequest;
import com.example.RideS.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    // Register endpoint
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
        String message = authService.register(request);
        return ResponseEntity.ok().body(message);
    }

    // Login endpoint
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        String token = authService.login(request);
        return ResponseEntity.ok().body(token);
    }
}
