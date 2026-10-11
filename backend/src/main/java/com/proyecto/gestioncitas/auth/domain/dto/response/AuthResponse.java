package com.proyecto.gestioncitas.auth.domain.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AuthResponse {
    private final String accessToken;
    private final String refreshToken;
    private final String tipo;
    private final String userId;
    private final String email;
    private final String rol;
}
