package com.thinhreal.applestore.controller;

import com.thinhreal.applestore.model.dto.auth.AuthResponse;
import com.thinhreal.applestore.model.dto.auth.LoginRequest;
import com.thinhreal.applestore.model.dto.auth.RegisterRequest;
import com.thinhreal.applestore.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @Valid @RequestBody LoginRequest request,
            HttpServletResponse response
    ) {
        return ResponseEntity.ok(authService.login(request, response));
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(
            @Valid @RequestBody RegisterRequest request,
            HttpServletResponse response
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.register(request, response));
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        return ResponseEntity.ok(authService.refresh(request, response));
    }

    @PostMapping("/logout")
    public ResponseEntity<AuthResponse> logout(
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        return ResponseEntity.ok(authService.logout(request, response));
    }

    @GetMapping("/me")
    public ResponseEntity<AuthResponse> me(HttpServletRequest request) {
        return ResponseEntity.ok(authService.currentUser(request));
    }
}
