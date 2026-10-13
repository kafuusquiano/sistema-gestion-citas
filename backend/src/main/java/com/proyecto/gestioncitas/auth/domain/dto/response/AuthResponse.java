package com.proyecto.gestioncitas.auth.domain.dto.response;

/**
 * Lo que devuelven /login, /register y /refresh.
 *
 * expiraEnSegundos lo usa el frontend para renovar el token antes de que
 * caduque, en lugar de esperar un 401 y reintentar.
 */
public record AuthResponse(
        String accessToken,
        String refreshToken,
        String tipo,
        long expiraEnSegundos,
        UsuarioResponse usuario) {

    private static final String TIPO_BEARER = "Bearer";

    public static AuthResponse de(String accessToken, String refreshToken,
            long expiraEnMilisegundos, UsuarioResponse usuario) {
        return new AuthResponse(accessToken, refreshToken, TIPO_BEARER,
                expiraEnMilisegundos / 1000, usuario);
    }
}
