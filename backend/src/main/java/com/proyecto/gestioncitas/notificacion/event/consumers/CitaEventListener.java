package com.proyecto.gestioncitas.notificacion.event.consumers;

import com.proyecto.gestioncitas.cita.event.CitaCanceladaEvent;
import com.proyecto.gestioncitas.cita.event.CitaCreadaEvent;
import com.proyecto.gestioncitas.cita.event.CitaReprogramadaEvent;
import com.proyecto.gestioncitas.notificacion.domain.enums.TipoNotificacion;
import com.proyecto.gestioncitas.notificacion.event.NotificacionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * Avisa al paciente de cada cambio en sus citas.
 *
 * Los tres metodos van con @Async: el envio de correo no debe retrasar la
 * respuesta de la operacion que lo origino. Si el aviso falla, la cita igual
 * quedo agendada, cancelada o movida.
 *
 * No se escucha cita.completada: avisar a alguien de que acaba de ser atendido
 * no aporta nada. El evento existe igual, porque otro modulo podria
 * necesitarlo mas adelante.
 *
 * Usa @TransactionalEventListener en fase AFTER_COMMIT y NO @EventListener.
 * La diferencia importa: @EventListener entrega el evento en el momento de
 * publicarlo, que es DENTRO de la transaccion que lo origino. Combinado con
 * @Async, el hilo receptor puede consultar la base antes de que esa
 * transaccion confirme, y entonces no encuentra los datos que necesita. Con
 * AFTER_COMMIT el aviso se prepara cuando la operacion ya quedo guardada.
 */
@Component
public class CitaEventListener {

    private static final Logger log = LoggerFactory.getLogger(CitaEventListener.class);

    private final NotificacionHandler handler;

    public CitaEventListener(NotificacionHandler handler) {
        this.handler = handler;
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void alCrearseUnaCita(CitaCreadaEvent evento) {
        log.debug("Aviso de cita creada: {}", evento.citaId());
        handler.sobreCita(TipoNotificacion.CITA_CONFIRMADA, evento.pacienteId(),
                evento.citaId(), evento.fechaHora(), evento.motivo());
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void alCancelarseUnaCita(CitaCanceladaEvent evento) {
        log.debug("Aviso de cita cancelada: {}", evento.citaId());
        handler.sobreCita(TipoNotificacion.CITA_CANCELADA, evento.pacienteId(),
                evento.citaId(), evento.fechaHora(), evento.motivo());
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void alReprogramarseUnaCita(CitaReprogramadaEvent evento) {
        log.debug("Aviso de cita reprogramada: {}", evento.citaId());
        handler.sobreCita(TipoNotificacion.CITA_REPROGRAMADA, evento.pacienteId(),
                evento.citaId(), evento.fechaHora(), evento.motivo());
    }
}
