package com.proyecto.gestioncitas.config.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtTokenProvider {
    private final JwtProperties props;

    private SecretKey key() {
        return Keys.hmacShaKeyFor(props.getSecret().getBytes(StandardCharsets.UTF_8));
    }

    public String generarAccessToken(String userId, String rol) {
        return Jwts.builder()
            .subject(userId)
            .claim("rol", rol)
            .issuedAt(new Date())
            .expiration(new Date(System.currentTimeMillis() + props.getExpirationMs()))
            .signWith(key())
            .compact();
    }

    public String generarRefreshToken(String userId) {
        return Jwts.builder()
            .subject(userId)
            .issuedAt(new Date())
            .expiration(new Date(System.currentTimeMillis() + props.getRefreshMs()))
            .signWith(key())
            .compact();
    }

    public Claims parsearToken(String token) {
        return Jwts.parser().verifyWith(key()).build()
            .parseSignedClaims(token).getPayload();
    }

    public boolean esValido(String token) {
        try { parsearToken(token); return true; }
        catch (JwtException | IllegalArgumentException e) {
            log.warn("Token JWT inválido: {}", e.getMessage());
            return false;
        }
    }

    public String getUserId(String token) { return parsearToken(token).getSubject(); }
    public String getRol(String token)    { return parsearToken(token).get("rol", String.class); }
}
