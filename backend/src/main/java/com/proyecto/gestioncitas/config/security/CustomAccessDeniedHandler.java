package com.proyecto.gestioncitas.config.security;

import com.proyecto.gestioncitas.common.dto.ErrorResponse;
import com.proyecto.gestioncitas.common.exception.ErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

/**
 * Responde 403 en JSON cuando el usuario esta autenticado pero su rol no
 * alcanza, segun la matriz de permisos de la seccion 2.4.
 */
@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    private static final Logger log = LoggerFactory.getLogger(CustomAccessDeniedHandler.class);

    private final ObjectMapper mapper;

    public CustomAccessDeniedHandler(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
            AccessDeniedException ex) throws IOException {

        ErrorCode codigo = ErrorCode.SIN_PERMISOS;
        log.warn("Acceso denegado a {} {}", request.getMethod(), request.getRequestURI());

        response.setStatus(codigo.getEstado().value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        ErrorResponse cuerpo = ErrorResponse.de(
                codigo.name(), codigo.getMensaje(), codigo.getEstado().value(), request.getRequestURI());

        mapper.writeValue(response.getWriter(), cuerpo);
    }
}
