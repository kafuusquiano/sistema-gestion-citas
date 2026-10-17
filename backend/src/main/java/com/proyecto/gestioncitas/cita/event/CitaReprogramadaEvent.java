package com.proyecto.gestioncitas.cita.event;

import com.proyecto.gestioncitas.common.constants.EventosDominio;
import com.proyecto.gestioncitas.common.event.DomainEvent;
import java.time.Instant;
import java.util.UUID;

/**
 * Una cita se movio a otra fecha y hora.
 *
 * Lo consume notificacion para avisar al paciente. El evento transporta los
 * datos que el correo necesita, de modo que el consumidor no tenga que
 * volver a consultar la cita ni conocer PacienteFacade.
 */
public record CitaReprogramadaEvent(
        UUID eventId,
        Instant ocurridoEn,
        Long citaId,
        Long pacienteId,
        Long medicoId,
        Instant fechaHora,
        String motivo) implements DomainEvent {

    @Override
    public String tipo() {
        return EventosDominio.CITA_REPROGRAMADA;
    }

    public static CitaReprogramadaEvent de(Long citaId, Long pacienteId, Long medicoId,
            Instant fechaHora, String motivo) {
        return new CitaReprogramadaEvent(UUID.randomUUID(), Instant.now(),
                citaId, pacienteId, medicoId, fechaHora, motivo);
    }
}
