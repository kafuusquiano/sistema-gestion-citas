package com.proyecto.gestioncitas.auth.service;

import com.proyecto.gestioncitas.auth.domain.dto.request.*;
import com.proyecto.gestioncitas.auth.domain.dto.response.AuthResponse;

public interface AuthService {
    AuthResponse registrar(RegisterRequest request);
    AuthResponse login(LoginRequest request);
    AuthResponse refrescarToken(RefreshTokenRequest request);
    void logout(String usuarioId);
}
