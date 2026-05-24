package com.fjconde.taskmanager.controller;

import com.fjconde.taskmanager.dto.auth.AuthResponse;
import com.fjconde.taskmanager.dto.auth.LoginRequest;
import com.fjconde.taskmanager.dto.auth.RegistroRequest;
import com.fjconde.taskmanager.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Autenticación", description = "Registro e inicio de sesión de usuarios")
public class AuthController {

    private final AuthService authService;

    // POST /api/auth/registro
    @Operation(summary = "Registrar nuevo usuario",
               description = "Crea una cuenta nueva y devuelve un token JWT listo para usar")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Usuario registrado correctamente"),
        @ApiResponse(responseCode = "400", description = "Email ya registrado o datos inválidos")
    })
    @PostMapping("/registro")
    public ResponseEntity<AuthResponse> registro(@Valid @RequestBody RegistroRequest request) {
        return ResponseEntity.ok(authService.registro(request));
    }

    // POST /api/auth/login
    @Operation(summary = "Iniciar sesión",
               description = "Autentica con email y contraseña y devuelve un token JWT")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Login correcto"),
        @ApiResponse(responseCode = "400", description = "Credenciales incorrectas o datos inválidos")
    })
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }
}
