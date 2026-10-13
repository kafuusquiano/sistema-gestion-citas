package com.proyecto.gestioncitas.auth.controller;

import com.proyecto.gestioncitas.auth.domain.dto.request.LoginRequest;
import com.proyecto.gestioncitas.auth.domain.dto.request.RefreshTokenRequest;
import com.proyecto.gestioncitas.auth.domain.dto.request.RegisterRequest;
import com.proyecto.gestioncitas.auth.domain.dto.response.AuthResponse;
import com.proyecto.gestioncitas.auth.service.AuthService;
import com.proyecto.gestioncitas.common.constants.AppConstants;
import com.proyecto.gestioncitas.common.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Endpoints publicos de autenticacion (instruccion G8).
 *
 * @SecurityRequirements vacio quita el candado de Swagger en estas rutas:
 * OpenApiConfig aplica el esquema Bearer a toda la API, y sin esta anotacion
 * la documentacion diria que /login necesita un token.
 */
@RestController
@RequestMapping("/api/auth")
@Tag(name = "Autenticacion", description = "Registro, inicio de sesion y manejo de tokens")
@SecurityRequirements
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    @Operation(summary = "Registrar un paciente nuevo",
            description = """
                    Crea la cuenta con rol PACIENTE y su ficha clinica, y devuelve
                    los tokens de sesion. A los medicos los da de alta un ADMIN
                    desde /api/medicos, porque hay que verificar su CMP.
                    """)
    public ResponseEntity<ApiResponse<AuthResponse>> registrar(
            @Valid @RequestBody RegisterRequest peticion) {

        AuthResponse respuesta = authService.registrar(peticion);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.exito("Registro completado correctamente", respuesta));
    }

    @PostMapping("/login")
    @Operation(summary = "Iniciar sesion",
            description = "Devuelve un access token de 1 hora y un refresh token de 7 dias.")
    public ResponseEntity<ApiResponse<AuthResponse>> iniciarSesion(
            @Valid @RequestBody LoginRequest peticion) {

        AuthResponse respuesta = authService.iniciarSesion(peticion);
        return ResponseEntity.ok(ApiResponse.exito("Sesion iniciada", respuesta));
    }

    @PostMapping("/refresh")
    @Operation(summary = "Renovar la sesion",
            description = """
                    Entrega un par de tokens nuevo. El refresh token enviado queda
                    revocado, asi que no puede reutilizarse.
                    """)
    public ResponseEntity<ApiResponse<AuthResponse>> renovar(
            @Valid @RequestBody RefreshTokenRequest peticion) {

        AuthResponse respuesta = authService.renovar(peticion.refreshToken());
        return ResponseEntity.ok(ApiResponse.exito("Sesion renovada", respuesta));
    }

    @PostMapping("/logout")
    @Operation(summary = "Cerrar sesion",
            description = """
                    Revoca el refresh token y pone el access token en la lista negra,
                    de modo que deja de servir antes de caducar.
                    """)
    public ResponseEntity<ApiResponse<Void>> cerrarSesion(
            @Valid @RequestBody RefreshTokenRequest peticion,
            HttpServletRequest request) {

        authService.cerrarSesion(peticion.refreshToken(), extraerAccessToken(request));
        return ResponseEntity.ok(ApiResponse.exito("Sesion cerrada"));
    }

    private String extraerAccessToken(HttpServletRequest request) {
        String header = request.getHeader(AppConstants.HEADER_AUTORIZACION);
        if (header == null || !header.startsWith(AppConstants.PREFIJO_BEARER)) {
            return null;
        }
        return header.substring(AppConstants.PREFIJO_BEARER.length()).trim();
    }
}
