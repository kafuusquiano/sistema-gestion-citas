package com.proyecto.gestioncitas.config.security;

import com.proyecto.gestioncitas.common.constants.AppConstants;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Extrae el token del header Authorization y arma la Authentication
 * (instruccion M5).
 *
 * La identidad se construye con los claims del token y no consultando la base
 * de datos, asi que no hace falta un UserDetailsService: una API stateless con
 * JWT se ahorra una consulta por peticion. El precio es que un cambio de rol
 * recien aplica cuando el access token caduca, que con una hora de vigencia es
 * aceptable para este sistema.
 *
 * El filtro nunca rechaza por su cuenta: si el token falta o es invalido, deja
 * el contexto vacio y sigue. Quien responde el 401 es JwtAuthenticationEntryPoint.
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    private final JwtTokenProvider proveedor;

    public JwtAuthenticationFilter(JwtTokenProvider proveedor) {
        this.proveedor = proveedor;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain chain) throws ServletException, IOException {

        String token = extraerToken(request);

        if (token != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            Claims claims = proveedor.leerClaimsONull(token);

            // Un refresh token no sirve para autenticar peticiones normales:
            // solo vale en /api/auth/refresh, que es una ruta publica.
            if (claims != null && proveedor.esAccessToken(claims)) {
                autenticar(request, claims);
            }
        }

        chain.doFilter(request, response);
    }

    private void autenticar(HttpServletRequest request, Claims claims) {
        try {
            Long usuarioId = proveedor.obtenerUsuarioId(claims);
            String rol = proveedor.obtenerRol(claims);

            // Spring Security espera el prefijo ROLE_ para que hasRole() funcione.
            var autoridades = rol == null
                    ? List.<SimpleGrantedAuthority>of()
                    : List.of(new SimpleGrantedAuthority("ROLE_" + rol));

            var auth = new UsernamePasswordAuthenticationToken(String.valueOf(usuarioId), null, autoridades);
            auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(auth);

            log.debug("Peticion autenticada: usuario {} con rol {}", usuarioId, rol);
        } catch (NumberFormatException e) {
            // El sub no es un numero: token manipulado. Se ignora y responde el entry point.
            log.debug("El claim sub no es un id valido");
        }
    }

    private String extraerToken(HttpServletRequest request) {
        String header = request.getHeader(AppConstants.HEADER_AUTORIZACION);
        if (header == null || !header.startsWith(AppConstants.PREFIJO_BEARER)) {
            return null;
        }
        String token = header.substring(AppConstants.PREFIJO_BEARER.length()).trim();
        return token.isEmpty() ? null : token;
    }
}
