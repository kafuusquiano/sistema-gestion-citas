package com.proyecto.gestioncitas.medico.domain.dto.request;

import com.proyecto.gestioncitas.medico.domain.enums.DiaSemana;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;
import java.time.LocalTime;

/**
 * Una franja de atencion. Cuerpo de los elementos de
 * PUT /api/medicos/{id}/horarios.
 */
public record HorarioRequest(

        @NotNull(message = "El dia de la semana es obligatorio")
        DiaSemana diaSemana,

        @NotNull(message = "La hora de inicio es obligatoria")
        LocalTime horaInicio,

        @NotNull(message = "La hora de fin es obligatoria")
        LocalTime horaFin,

        /**
         * Minutos por turno. Si no se envia, se toma la duracion que sugiere
         * la subclase del medico: 20 para un general, 30 para un especialista.
         */
        Short duracionSlotMinutos) {

    @AssertTrue(message = "La hora de fin debe ser posterior a la de inicio")
    public boolean isRangoValido() {
        return horaInicio == null || horaFin == null || horaFin.isAfter(horaInicio);
    }

    /** El CHECK ck_horarios_slot de la base solo admite estos valores. */
    @AssertTrue(message = "La duracion del turno debe ser 10, 15, 20, 30, 45 o 60 minutos")
    public boolean isDuracionValida() {
        if (duracionSlotMinutos == null) {
            return true;
        }
        return switch (duracionSlotMinutos) {
            case 10, 15, 20, 30, 45, 60 -> true;
            default -> false;
        };
    }

    /**
     * La franja tiene que alcanzar para al menos un turno completo.
     *
     * Sin esta validacion se podria registrar un horario de 08:00 a 08:10 con
     * turnos de 30 minutos, que no genera ningun slot y aparece como un medico
     * que atiende pero nunca tiene cupo.
     */
    @AssertTrue(message = "La franja no alcanza para un turno completo")
    public boolean isFranjaSuficiente() {
        if (horaInicio == null || horaFin == null || duracionSlotMinutos == null
                || !horaFin.isAfter(horaInicio)) {
            return true;
        }
        long minutos = java.time.Duration.between(horaInicio, horaFin).toMinutes();
        return minutos >= duracionSlotMinutos;
    }
}
