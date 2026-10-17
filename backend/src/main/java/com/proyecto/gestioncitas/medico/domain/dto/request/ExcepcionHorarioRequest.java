package com.proyecto.gestioncitas.medico.domain.dto.request;

import com.proyecto.gestioncitas.medico.domain.enums.MotivoExcepcion;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

/**
 * Cuerpo de POST /api/medicos/{id}/excepciones y de
 * POST /api/medicos/excepciones para un feriado del establecimiento.
 */
public record ExcepcionHorarioRequest(

        @NotNull(message = "La fecha de inicio es obligatoria")
        LocalDate fechaInicio,

        @NotNull(message = "La fecha de fin es obligatoria")
        LocalDate fechaFin,

        @NotNull(message = "El motivo es obligatorio")
        MotivoExcepcion motivo,

        @Size(max = 200)
        String descripcion) {

    @AssertTrue(message = "La fecha de fin no puede ser anterior a la de inicio")
    public boolean isRangoValido() {
        return fechaInicio == null || fechaFin == null || !fechaFin.isBefore(fechaInicio);
    }

    /**
     * Un dia libre se registra con la misma fecha en los dos extremos, asi
     * que el rango de un solo dia es valido.
     */
    public boolean esDeUnSoloDia() {
        return fechaInicio != null && fechaInicio.equals(fechaFin);
    }
}
