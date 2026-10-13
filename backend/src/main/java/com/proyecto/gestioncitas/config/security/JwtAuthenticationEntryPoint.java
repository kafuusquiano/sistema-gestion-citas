package com.proyecto.gestioncitas.config.security;

import com.proyecto.gestioncitas.common.dto.ErrorResponse;
import com.proyecto.gestioncitas.common.exception.ErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

/**
 * Responde 401 en JSON cuando la peticion llega sin token valido.
 *
 * Sin esto, Spring Security devolveria una pagina HTML de login, que para una
 * API consumida por React no sirve.
 *
 * ObjectMapper viene de tools.jackson: Spring Boot 4 usa Jackson 3. Importar
 * com.fasterxml.jackson aqui compilaria pero inyectaria el mapper equivocado.
 */
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper mapper;

    public JwtAuthenticationEntryPoint(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException authException) throws IOException {

        ErrorCode codigo = ErrorCode.NO_AUTENTICADO;

        response.setStatus(codigo.getEstado().value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        ErrorResponse cuerpo = ErrorResponse.de(
                codigo.name(), codigo.getMensaje(), codigo.getEstado().value(), request.getRequestURI());

        mapper.writeValue(response.getWriter(), cuerpo);
    }
}
