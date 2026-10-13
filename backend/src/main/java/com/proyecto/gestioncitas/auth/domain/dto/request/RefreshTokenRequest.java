package com.proyecto.gestioncitas.auth.domain.dto.request;

import jakarta.validation.constraints.NotBlank;

/**
 * Cuerpo de POST /api/auth/refresh y de POST /api/auth/logout.
 */
public record RefreshTokenRequest(

        @NotBlank(message = "El refresh token es obligatorio")
        String refreshToken) {
}
