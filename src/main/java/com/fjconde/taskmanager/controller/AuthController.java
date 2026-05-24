package com.fjconde.taskmanager.controller;

import com.fjconde.taskmanager.dto.auth.AuthResponse;
import com.fjconde.taskmanager.dto.auth.LoginRequest;
import com.fjconde.taskmanager.dto.auth.RegistroRequest;
import com.fjconde.taskmanager.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador para registro e inicio de sesión.
 * Rutas públicas — no requieren token JWT.
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    // POST /api/auth/registro
    @PostMapping("/registro")
    public ResponseEntity<AuthResponse> registro(@Valid @RequestBody RegistroRequest request) {
        return ResponseEntity.ok(authService.registro(request));
    }

    // POST /api/auth/login
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }
}
