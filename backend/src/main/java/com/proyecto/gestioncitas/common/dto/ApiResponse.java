package com.proyecto.gestioncitas.common.dto;

import java.time.Instant;

/**
 * Envoltorio unico de toda respuesta REST (instruccion G3).
 *
 * Forma: { success, message, data, timestamp }
 */
public record ApiResponse<T>(
        boolean success,
        String message,
        T data,
        Instant timestamp) {

    public static <T> ApiResponse<T> exito(String mensaje, T datos) {
        return new ApiResponse<>(true, mensaje, datos, Instant.now());
    }

    public static <T> ApiResponse<T> exito(T datos) {
        return exito("Operacion realizada correctamente", datos);
    }

    /** Para operaciones que no devuelven cuerpo, como una eliminacion. */
    public static ApiResponse<Void> exito(String mensaje) {
        return new ApiResponse<>(true, mensaje, null, Instant.now());
    }

    public static <T> ApiResponse<T> error(String mensaje) {
        return new ApiResponse<>(false, mensaje, null, Instant.now());
    }

    public static <T> ApiResponse<T> error(String mensaje, T detalle) {
        return new ApiResponse<>(false, mensaje, detalle, Instant.now());
    }
}
