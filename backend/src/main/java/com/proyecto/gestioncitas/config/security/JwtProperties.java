package com.proyecto.gestioncitas.config.security;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

/**
 * Parametros del JWT, leidos de app.jwt.* y alimentados por variables de
 * entorno (instruccion G5). Nunca se hardcodea la clave.
 *
 * El minimo de 64 caracteres no es decorativo: HS512 firma con una clave de
 * 512 bits, y JJWT rechaza con WeakKeyException cualquier clave mas corta.
 */
@Validated
@ConfigurationProperties(prefix = "app.jwt")
public record JwtProperties(

        @NotBlank(message = "Falta JWT_SECRET")
        @Size(min = 64, message = "JWT_SECRET debe tener al menos 64 caracteres para firmar con HS512")
        String secret,

        /** Vigencia del access token. Por defecto 1 hora. */
        @Positive long expirationMs,

        /** Vigencia del refresh token. Por defecto 7 dias. */
        @Positive long refreshMs) {
}
