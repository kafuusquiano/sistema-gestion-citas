package com.proyecto.gestioncitas.auth.service;

import com.proyecto.gestioncitas.auth.domain.dto.request.LoginRequest;
import com.proyecto.gestioncitas.auth.domain.dto.request.RegisterRequest;
import com.proyecto.gestioncitas.auth.domain.dto.response.AuthResponse;

/**
 * Registro, inicio de sesion, renovacion y cierre de sesion.
 */
public interface AuthService {

    /** Registro publico. Siempre crea un usuario con rol PACIENTE. */
    AuthResponse registrar(RegisterRequest peticion);

    AuthResponse iniciarSesion(LoginRequest peticion);

    /** Entrega un par de tokens nuevo y revoca el refresh token usado. */
    AuthResponse renovar(String refreshToken);

    /**
     * Cierra la sesion: revoca el refresh token y pone el access token en la
     * lista negra.
     *
     * @param accessToken token del header Authorization, puede ser nulo
     */
    void cerrarSesion(String refreshToken, String accessToken);
}
