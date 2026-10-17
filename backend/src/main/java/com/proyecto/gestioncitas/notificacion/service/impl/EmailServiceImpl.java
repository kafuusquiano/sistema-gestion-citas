package com.proyecto.gestioncitas.notificacion.service.impl;

import com.proyecto.gestioncitas.notificacion.exception.NotificacionException;
import com.proyecto.gestioncitas.notificacion.service.EmailService;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

/**
 * Envia correo con JavaMailSender.
 *
 * El remitente se inyecta con ObjectProvider y la direccion de origen tiene un
 * valor por defecto, de modo que la aplicacion arranca igual sin correo
 * configurado. Es la misma decision que se tomo con Redis: un servicio
 * accesorio no debe ser requisito para desplegar.
 */
@Service
public class EmailServiceImpl implements EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailServiceImpl.class);

    private final ObjectProvider<JavaMailSender> mailSender;
    private final String remitente;
    private final boolean habilitado;

    public EmailServiceImpl(ObjectProvider<JavaMailSender> mailSender,
            @Value("${app.correo.remitente:no-responder@acapulco.gob.pe}") String remitente,
            @Value("${app.correo.habilitado:true}") boolean habilitado) {
        this.mailSender = mailSender;
        this.remitente = remitente;
        this.habilitado = habilitado;
    }

    @Override
    public void enviar(String destinatario, String asunto, String cuerpo) {
        JavaMailSender sender = mailSender.getIfAvailable();
        if (sender == null) {
            throw new NotificacionException("No hay servidor de correo configurado");
        }

        try {
            MimeMessage mensaje = sender.createMimeMessage();
            // true en el segundo parametro: mensaje multiparte, necesario para
            // enviar HTML. true en setText: el cuerpo se interpreta como HTML.
            MimeMessageHelper helper = new MimeMessageHelper(mensaje, true, "UTF-8");
            helper.setFrom(remitente);
            helper.setTo(destinatario);
            helper.setSubject(asunto);
            helper.setText(cuerpo, true);

            sender.send(mensaje);
        } catch (MailException | jakarta.mail.MessagingException e) {
            // Se envuelve para que el servicio la registre en la notificacion
            // y el reintento la recoja despues.
            throw new NotificacionException(
                    "No se pudo enviar el correo a %s: %s".formatted(destinatario, e.getMessage()), e);
        }
    }

    @Override
    public boolean estaConfigurado() {
        if (!habilitado) {
            return false;
        }
        boolean hay = mailSender.getIfAvailable() != null;
        if (!hay) {
            log.debug("Sin servidor de correo: los avisos quedaran solo en la aplicacion");
        }
        return hay;
    }
}
