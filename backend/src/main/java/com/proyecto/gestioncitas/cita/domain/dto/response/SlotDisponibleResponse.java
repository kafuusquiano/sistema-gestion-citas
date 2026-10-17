package com.proyecto.gestioncitas.cita.domain.dto.response;

import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * Un turno libre en la agenda de un medico.
 *
 * Lo produce DisponibilidadService al proyectar la plantilla semanal del
 * medico sobre una fecha, restarle las excepciones y quitarle las citas ya
 * reservadas.
 */
public record SlotDisponibleResponse(
        LocalDateTime fechaHora,
        LocalTime hora,
        int duracionMinutos,
        boolean disponible) {

    public static SlotDisponibleResponse libre(LocalDateTime fechaHora, int duracionMinutos) {
        return new SlotDisponibleResponse(fechaHora, fechaHora.toLocalTime(),
                duracionMinutos, true);
    }

    /**
     * Un turno ocupado tambien se devuelve, marcado como no disponible, para
     * que el calendario del frontend pueda mostrarlo en gris en lugar de
     * ocultarlo. Ver el cupo tomado comunica mas que no verlo.
     */
    public static SlotDisponibleResponse ocupado(LocalDateTime fechaHora, int duracionMinutos) {
        return new SlotDisponibleResponse(fechaHora, fechaHora.toLocalTime(),
                duracionMinutos, false);
    }
}
