package com.proyecto.gestioncitas.medico.event.producers;

import com.proyecto.gestioncitas.common.event.EventPublisher;
import com.proyecto.gestioncitas.medico.event.HorarioActualizadoEvent;
import org.springframework.stereotype.Component;

/**
 * Publica los eventos del modulo medico, todos en un solo archivo para que se
 * vea de un vistazo que emite este modulo.
 */
@Component
public class MedicoEventProducer {

    private final EventPublisher publisher;

    public MedicoEventProducer(EventPublisher publisher) {
        this.publisher = publisher;
    }

    public void horarioActualizado(Long medicoId) {
        publisher.publicar(HorarioActualizadoEvent.porCambioDeHorario(medicoId));
    }

    public void excepcionRegistrada(Long medicoId) {
        publisher.publicar(HorarioActualizadoEvent.porExcepcion(medicoId));
    }

    public void estadoCambiado(Long medicoId) {
        publisher.publicar(HorarioActualizadoEvent.porCambioDeEstado(medicoId));
    }
}
