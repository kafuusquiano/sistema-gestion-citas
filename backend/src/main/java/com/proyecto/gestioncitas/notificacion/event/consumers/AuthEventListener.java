package com.proyecto.gestioncitas.notificacion.event.consumers;

import com.proyecto.gestioncitas.auth.event.UsuarioRegistradoEvent;
import com.proyecto.gestioncitas.notificacion.event.NotificacionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * Envia el correo de bienvenida al registrarse un usuario.
 *
 * Lleva @Async a proposito, al reves que el listener del modulo paciente.
 * Aquel crea la ficha clinica y debe correr en la misma transaccion para que
 * el registro sea atomico; este solo manda un correo, y nadie debe esperar al
 * servidor SMTP para terminar de registrarse.
 *
 * Como consecuencia, si el correo falla el registro igual queda hecho. Es lo
 * correcto: la cuenta existe y el aviso puede reintentarse despues.
 *
 * Usa @TransactionalEventListener en fase AFTER_COMMIT y NO @EventListener.
 * La diferencia importa: @EventListener entrega el evento en el momento de
 * publicarlo, que es DENTRO de la transaccion que lo origino. Combinado con
 * @Async, el hilo receptor puede consultar la base antes de que esa
 * transaccion confirme, y entonces no encuentra los datos que necesita. Con
 * AFTER_COMMIT el aviso se prepara cuando la operacion ya quedo guardada.
 */
@Component
public class AuthEventListener {

    private static final Logger log = LoggerFactory.getLogger(AuthEventListener.class);

    private final NotificacionHandler handler;

    public AuthEventListener(NotificacionHandler handler) {
        this.handler = handler;
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void alRegistrarseUnUsuario(UsuarioRegistradoEvent evento) {
        log.debug("Preparando bienvenida para el usuario {}", evento.usuarioId());
        handler.bienvenida(evento.usuarioId(), evento.nombreCompleto());
    }
}
