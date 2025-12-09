package com.example.RideS.service;

import com.example.RideS.dto.LoginRequest;
import com.example.RideS.dto.RegisterRequest;
import com.example.RideS.exception.BadRequestException;
import com.example.RideS.model.User;
import com.example.RideS.repository.UserRepository;
import com.example.RideS.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    public String register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new BadRequestException("Username already exists");
        }

        if (!request.getRole().equals("ROLE_USER") && !request.getRole().equals("ROLE_DRIVER")) {
            throw new BadRequestException("Role must be ROLE_USER or ROLE_DRIVER");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(request.getRole());

        userRepository.save(user);

        return "User registered successfully";
    }

    public String login(LoginRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new BadRequestException("Invalid username or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BadRequestException("Invalid username or password");
        }

        // Generate JWT
        return jwtUtil.generateToken(user.getUsername(), user.getRole());
    }
}
