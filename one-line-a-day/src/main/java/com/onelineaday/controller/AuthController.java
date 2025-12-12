package com.onelineaday.controller;

import com.onelineaday.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService service;

    @PostMapping("/register")
    public ResponseEntity<UserService.AuthResponse> register(
            @RequestBody @jakarta.validation.Valid UserService.RegisterRequest request) { // Added @Valid
        return ResponseEntity.ok(service.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<UserService.AuthResponse> login(
            @RequestBody @jakarta.validation.Valid UserService.AuthRequest request) { // Added @Valid
        return ResponseEntity.ok(service.authenticate(request));
    }
}
