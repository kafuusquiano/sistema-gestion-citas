package com.proyecto.gestioncitas.config.security;

import com.proyecto.gestioncitas.common.constants.AppConstants;
import com.proyecto.gestioncitas.common.exception.BusinessException;
import com.proyecto.gestioncitas.common.exception.ErrorCode;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;
import javax.crypto.SecretKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Emision y lectura de tokens JWT.
 *
 * El access token lleva sub (id del usuario), rol y exp (instruccion M5). El
 * refresh token lleva ademas un jti, que es el identificador con el que
 * TokenBlacklistService puede revocarlo al cerrar sesion.
 *
 * API de JJWT 0.13: signWith(clave, Jwts.SIG.HS512) y
 * parser().verifyWith(clave). Las formas antiguas con SignatureAlgorithm
 * estan deprecadas.
 */
@Component
public class JwtTokenProvider {

    private static final Logger log = LoggerFactory.getLogger(JwtTokenProvider.class);

    private final JwtProperties propiedades;
    private final SecretKey clave;

    public JwtTokenProvider(JwtProperties propiedades) {
        this.propiedades = propiedades;
        this.clave = Keys.hmacShaKeyFor(propiedades.secret().getBytes(StandardCharsets.UTF_8));
    }

    public String generarAccessToken(Long usuarioId, String rol) {
        Instant ahora = Instant.now();
        return Jwts.builder()
                // El access token tambien lleva jti: es lo que permite
                // revocarlo al cerrar sesion, poniendolo en la lista negra.
                .id(UUID.randomUUID().toString())
                .subject(String.valueOf(usuarioId))
                .claim(AppConstants.CLAIM_ROL, rol)
                .claim(AppConstants.CLAIM_TIPO, AppConstants.TIPO_ACCESS)
                .issuedAt(Date.from(ahora))
                .expiration(Date.from(ahora.plusMillis(propiedades.expirationMs())))
                .signWith(clave, Jwts.SIG.HS512)
                .compact();
    }

    /**
     * El jti que se devuelve en el token es el que el modulo auth guarda para
     * poder revocarlo despues.
     */
    public String generarRefreshToken(Long usuarioId, String jti) {
        Instant ahora = Instant.now();
        return Jwts.builder()
                .id(jti)
                .subject(String.valueOf(usuarioId))
                .claim(AppConstants.CLAIM_TIPO, AppConstants.TIPO_REFRESH)
                .issuedAt(Date.from(ahora))
                .expiration(Date.from(ahora.plusMillis(propiedades.refreshMs())))
                .signWith(clave, Jwts.SIG.HS512)
                .compact();
    }

    public String generarJti() {
        return UUID.randomUUID().toString();
    }

    /**
     * Lee los claims verificando la firma.
     *
     * @throws BusinessException con TOKEN_EXPIRADO o TOKEN_INVALIDO, para que
     *         GlobalExceptionHandler lo traduzca a 401 con mensaje en espanol.
     */
    public Claims leerClaims(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(clave)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (ExpiredJwtException e) {
            throw new BusinessException(ErrorCode.TOKEN_EXPIRADO);
        } catch (JwtException | IllegalArgumentException e) {
            throw new BusinessException(ErrorCode.TOKEN_INVALIDO);
        }
    }

    /**
     * Version silenciosa para el filtro: devuelve null en vez de lanzar, de
     * modo que la peticion siga su curso sin autenticacion y la rechace el
     * entry point con un 401 limpio.
     */
    public Claims leerClaimsONull(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(clave)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (ExpiredJwtException e) {
            log.debug("Token expirado");
            return null;
        } catch (JwtException | IllegalArgumentException e) {
            log.debug("Token invalido: {}", e.getMessage());
            return null;
        }
    }

    public Long obtenerUsuarioId(Claims claims) {
        return Long.valueOf(claims.getSubject());
    }

    public String obtenerRol(Claims claims) {
        return claims.get(AppConstants.CLAIM_ROL, String.class);
    }

    public boolean esRefreshToken(Claims claims) {
        return AppConstants.TIPO_REFRESH.equals(claims.get(AppConstants.CLAIM_TIPO, String.class));
    }

    public boolean esAccessToken(Claims claims) {
        return AppConstants.TIPO_ACCESS.equals(claims.get(AppConstants.CLAIM_TIPO, String.class));
    }

    public String obtenerJti(Claims claims) {
        return claims.getId();
    }

    /** Lo que queda de vigencia, para fijar el TTL del token revocado en Redis. */
    public long milisegundosRestantes(Claims claims) {
        Date exp = claims.getExpiration();
        return exp == null ? 0 : Math.max(0, exp.getTime() - System.currentTimeMillis());
    }

    public long getExpirationMs() {
        return propiedades.expirationMs();
    }

    public long getRefreshMs() {
        return propiedades.refreshMs();
    }
}
