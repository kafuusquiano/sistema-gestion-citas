package com.proyecto.gestioncitas.medico.event;

import com.proyecto.gestioncitas.common.constants.EventosDominio;
import com.proyecto.gestioncitas.common.event.DomainEvent;
import java.time.Instant;
import java.util.UUID;

/**
 * El horario de un medico cambio.
 *
 * Lo consume el modulo cita para invalidar la cache de slots: sin eso,
 * DisponibilidadService seguiria ofreciendo los turnos del horario anterior
 * durante los 5 minutos de TTL, y un paciente podria reservar una hora en la
 * que el medico ya no atiende.
 *
 * Tambien se publica al registrar una excepcion, porque el efecto sobre la
 * disponibilidad es el mismo.
 */
public record HorarioActualizadoEvent(
        UUID eventId,
        Instant ocurridoEn,
        Long medicoId,
        String motivo) implements DomainEvent {

    @Override
    public String tipo() {
        return EventosDominio.MEDICO_HORARIO_ACTUALIZADO;
    }

    public static HorarioActualizadoEvent porCambioDeHorario(Long medicoId) {
        return new HorarioActualizadoEvent(UUID.randomUUID(), Instant.now(), medicoId,
                "Se modifico la plantilla semanal de atencion");
    }

    public static HorarioActualizadoEvent porExcepcion(Long medicoId) {
        return new HorarioActualizadoEvent(UUID.randomUUID(), Instant.now(), medicoId,
                "Se registro una excepcion de horario");
    }

    public static HorarioActualizadoEvent porCambioDeEstado(Long medicoId) {
        return new HorarioActualizadoEvent(UUID.randomUUID(), Instant.now(), medicoId,
                "Cambio el estado del medico");
    }
}
