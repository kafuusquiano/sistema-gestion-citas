package com.proyecto.gestioncitas.cita.event.producers;

import com.proyecto.gestioncitas.cita.domain.entity.Cita;
import com.proyecto.gestioncitas.cita.event.CitaCanceladaEvent;
import com.proyecto.gestioncitas.cita.event.CitaCompletadaEvent;
import com.proyecto.gestioncitas.cita.event.CitaCreadaEvent;
import com.proyecto.gestioncitas.cita.event.CitaReprogramadaEvent;
import com.proyecto.gestioncitas.common.event.EventPublisher;
import org.springframework.stereotype.Component;

/**
 * Publica los eventos del modulo cita, todos en un solo archivo para que se
 * vea de un vistazo que emite este modulo.
 */
@Component
public class CitaEventProducer {

    private final EventPublisher publisher;

    public CitaEventProducer(EventPublisher publisher) {
        this.publisher = publisher;
    }

    public void citaCreada(Cita cita) {
        publisher.publicar(CitaCreadaEvent.de(cita.getId(), cita.getPacienteId(),
                cita.getMedicoId(), cita.getFechaHora(), cita.getMotivo()));
    }

    public void citaCancelada(Cita cita) {
        publisher.publicar(CitaCanceladaEvent.de(cita.getId(), cita.getPacienteId(),
                cita.getMedicoId(), cita.getFechaHora(), cita.getMotivoCancelacion()));
    }

    public void citaReprogramada(Cita cita, String detalle) {
        publisher.publicar(CitaReprogramadaEvent.de(cita.getId(), cita.getPacienteId(),
                cita.getMedicoId(), cita.getFechaHora(), detalle));
    }

    public void citaCompletada(Cita cita) {
        publisher.publicar(CitaCompletadaEvent.de(cita.getId(), cita.getPacienteId(),
                cita.getMedicoId(), cita.getFechaHora(), cita.getObservaciones()));
    }
}
