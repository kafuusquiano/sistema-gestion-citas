package com.proyecto.gestioncitas.config.web;

import java.util.Arrays;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

/**
 * CORS para que el frontend en Vercel pueda llamar al backend en Render.
 *
 * Los origenes llegan de CORS_ALLOWED_ORIGINS, separados por coma. La URL debe
 * ir exacta, con https:// y sin barra final: el navegador compara el origen
 * caracter por caracter y una barra de mas basta para que bloquee la peticion.
 */
@Configuration
public class CorsConfig {

    private final List<String> origenesPermitidos;

    public CorsConfig(@Value("${app.cors.allowed-origins}") String origenes) {
        this.origenesPermitidos = Arrays.stream(origenes.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toList();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(origenesPermitidos);
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("Authorization", "Content-Type", "Accept"));
        // El frontend no lee cookies del backend: la sesion vive en el token.
        config.setAllowCredentials(false);
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource fuente = new UrlBasedCorsConfigurationSource();
        fuente.registerCorsConfiguration("/api/**", config);
        return fuente;
    }
}
