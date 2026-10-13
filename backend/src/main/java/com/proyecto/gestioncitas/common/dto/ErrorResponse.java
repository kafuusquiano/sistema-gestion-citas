package com.proyecto.gestioncitas.common.dto;

import java.time.Instant;
import java.util.List;
import java.util.Map;

/**
 * Cuerpo de error. Nunca lleva stack trace (instruccion G4).
 *
 * El campo errores solo aparece en fallos de validacion, con un mensaje por campo.
 */
public record ErrorResponse(
        String codigo,
        String mensaje,
        int estado,
        String ruta,
        Map<String, List<String>> errores,
        Instant timestamp) {

    public static ErrorResponse de(String codigo, String mensaje, int estado, String ruta) {
        return new ErrorResponse(codigo, mensaje, estado, ruta, null, Instant.now());
    }

    public static ErrorResponse deValidacion(String codigo, String mensaje, int estado, String ruta,
            Map<String, List<String>> errores) {
        return new ErrorResponse(codigo, mensaje, estado, ruta, errores, Instant.now());
    }
}
