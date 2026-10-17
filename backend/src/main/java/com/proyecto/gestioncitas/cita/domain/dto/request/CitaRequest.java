package com.proyecto.gestioncitas.cita.domain.dto.request;

import com.proyecto.gestioncitas.cita.domain.enums.TipoCita;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

/**
 * Cuerpo de POST /api/citas.
 *
 * pacienteId es opcional: si lo omite un PACIENTE, el sistema usa su propia
 * ficha. Un ADMIN si debe indicarlo, porque agenda en nombre de otro.
 */
public record CitaRequest(

        /** Nulo cuando el propio paciente agenda para si mismo. */
        Long pacienteId,

        @NotNull(message = "El medico es obligatorio")
        Long medicoId,

        @NotNull(message = "La fecha y hora son obligatorias")
        @Future(message = "No se puede agendar una cita en el pasado")
        LocalDateTime fechaHora,

        TipoCita tipo,

        @Size(max = 300)
        String motivo) {

    /** Si no se indica el tipo, se asume una consulta. */
    public TipoCita tipoOConsulta() {
        return tipo == null ? TipoCita.CONSULTA : tipo;
    }
}
