package com.proyecto.gestioncitas.notificacion.service;

/**
 * Envio de correo. Lo usa EmailSender, que es quien decide cuando.
 */
public interface EmailService {

    /**
     * @param cuerpo HTML ya renderizado por TemplateRenderer
     * @throws com.proyecto.gestioncitas.notificacion.exception.NotificacionException
     *         si el servidor de correo rechaza el envio
     */
    void enviar(String destinatario, String asunto, String cuerpo);

    /**
     * true si hay un servidor de correo configurado.
     *
     * Permite que el sistema funcione sin correo: sin esta comprobacion, cada
     * aviso acumularia intentos fallidos contra un servidor inexistente.
     */
    boolean estaConfigurado();
}
