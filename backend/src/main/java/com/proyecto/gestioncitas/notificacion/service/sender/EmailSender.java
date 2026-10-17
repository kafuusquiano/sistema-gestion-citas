package com.proyecto.gestioncitas.notificacion.service.sender;

import com.proyecto.gestioncitas.notificacion.domain.entity.Notificacion;
import com.proyecto.gestioncitas.notificacion.domain.enums.CanalNotificacion;
import com.proyecto.gestioncitas.notificacion.exception.NotificacionException;
import com.proyecto.gestioncitas.notificacion.service.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Entrega el aviso por correo electronico.
 *
 * A diferencia del canal dentro de la aplicacion, aqui si hay un servicio
 * externo que puede fallar o no estar configurado. De ahi que disponible()
 * consulte al EmailService antes de intentar nada.
 */
@Component
public class EmailSender implements NotificacionSender {

    private static final Logger log = LoggerFactory.getLogger(EmailSender.class);

    private final EmailService emailService;

    public EmailSender(EmailService emailService) {
        this.emailService = emailService;
    }

    @Override
    public CanalNotificacion canal() {
        return CanalNotificacion.EMAIL;
    }

    @Override
    public void enviar(Notificacion notificacion) {
        String destino = notificacion.getCorreoDestino();
        if (destino == null || destino.isBlank()) {
            throw new NotificacionException(
                    "El destinatario %d no tiene correo registrado"
                            .formatted(notificacion.getDestinatarioId()));
        }

        emailService.enviar(destino, notificacion.getAsunto(), notificacion.getMensaje());
        notificacion.marcarEnviada();

        log.debug("Correo enviado a {}: {}", destino, notificacion.getAsunto());
    }

    @Override
    public boolean disponible() {
        return emailService.estaConfigurado();
    }
}
