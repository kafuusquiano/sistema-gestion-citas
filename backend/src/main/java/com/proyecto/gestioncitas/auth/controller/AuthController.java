package com.proyecto.gestioncitas.auth.controller;

import com.proyecto.gestioncitas.auth.domain.dto.request.*;
import com.proyecto.gestioncitas.auth.domain.dto.response.AuthResponse;
import com.proyecto.gestioncitas.auth.service.AuthService;
import com.proyecto.gestioncitas.common.dto.ApiResponse;
import com.proyecto.gestioncitas.common.util.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Autenticación", description = "Registro, login y gestión de tokens")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    @Operation(summary = "Registrar nuevo usuario")
    public ResponseEntity<ApiResponse<AuthResponse>> registrar(
            @Valid @RequestBody RegisterRequest request) {
        AuthResponse data = authService.registrar(request);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.<AuthResponse>builder()
                .success(true).message("Usuario registrado correctamente").data(data).build());
    }

    @PostMapping("/login")
    @Operation(summary = "Iniciar sesión")
    public ResponseEntity<ApiResponse<AuthResponse>> login(
            @Valid @RequestBody LoginRequest request) {
        AuthResponse data = authService.login(request);
        return ResponseEntity.ok(ApiResponse.<AuthResponse>builder()
            .success(true).message("Login exitoso").data(data).build());
    }

    @PostMapping("/refresh")
    @Operation(summary = "Refrescar access token")
    public ResponseEntity<ApiResponse<AuthResponse>> refresh(
            @Valid @RequestBody RefreshTokenRequest request) {
        AuthResponse data = authService.refrescarToken(request);
        return ResponseEntity.ok(ApiResponse.<AuthResponse>builder()
            .success(true).message("Token renovado").data(data).build());
    }

    @PostMapping("/logout")
    @Operation(summary = "Cerrar sesión")
    public ResponseEntity<ApiResponse<Void>> logout() {
        authService.logout(SecurityUtils.getCurrentUserId());
        return ResponseEntity.ok(ApiResponse.<Void>builder()
            .success(true).message("Sesión cerrada correctamente").build());
    }
}
