package com.proyecto.gestioncitas.cita.domain.dto.request;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

/**
 * Cuerpo de PATCH /api/citas/{id}/reprogramar.
 *
 * La cita conserva su identificador: se mueve de fecha, no se crea otra. La
 * fecha anterior queda en el historial.
 */
public record ReprogramarRequest(

        @NotNull(message = "La nueva fecha y hora son obligatorias")
        @Future(message = "La nueva fecha debe ser futura")
        LocalDateTime nuevaFechaHora,

        @Size(max = 300)
        String motivo) {
}
