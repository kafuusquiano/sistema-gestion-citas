package com.proyecto.gestioncitas.medico.domain.dto.response;

import com.proyecto.gestioncitas.medico.domain.enums.MotivoExcepcion;
import java.time.LocalDate;

/**
 * Rango de fechas sin atencion.
 *
 * medicoId nulo y general en true significan que aplica a todo el
 * establecimiento, como un feriado.
 */
public record ExcepcionHorarioResponse(
        Long id,
        Long medicoId,
        boolean general,
        LocalDate fechaInicio,
        LocalDate fechaFin,
        MotivoExcepcion motivo,
        String descripcion) {
}
