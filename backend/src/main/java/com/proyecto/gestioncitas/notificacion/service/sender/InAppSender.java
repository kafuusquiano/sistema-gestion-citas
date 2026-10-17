package com.proyecto.gestioncitas.notificacion.service.sender;

import com.proyecto.gestioncitas.notificacion.domain.entity.Notificacion;
import com.proyecto.gestioncitas.notificacion.domain.enums.CanalNotificacion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Entrega el aviso dentro de la aplicacion.
 *
 * La entrega consiste en marcarlo como enviado: la notificacion ya esta en la
 * base de datos y el paciente la vera en la campana al entrar. No hay servicio
 * externo del que depender, asi que este canal nunca falla.
 */
@Component
public class InAppSender implements NotificacionSender {

    private static final Logger log = LoggerFactory.getLogger(InAppSender.class);

    @Override
    public CanalNotificacion canal() {
        return CanalNotificacion.IN_APP;
    }

    @Override
    public void enviar(Notificacion notificacion) {
        notificacion.marcarEnviada();
        log.debug("Aviso en aplicacion disponible para el usuario {}: {}",
                notificacion.getDestinatarioId(), notificacion.getAsunto());
    }

    /** Siempre. No depende de ningun servicio externo. */
    @Override
    public boolean disponible() {
        return true;
    }
}
