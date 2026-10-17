package com.proyecto.gestioncitas.medico.domain.dto.response;

import com.proyecto.gestioncitas.medico.domain.enums.DiaSemana;
import java.time.LocalTime;

/**
 * Una franja de atencion. cantidadSlots sale del metodo de la entidad, para
 * que el frontend muestre "12 cupos" sin recalcularlo.
 */
public record HorarioResponse(
        Long id,
        DiaSemana diaSemana,
        LocalTime horaInicio,
        LocalTime horaFin,
        short duracionSlotMinutos,
        int cantidadSlots,
        boolean activo) {
}
