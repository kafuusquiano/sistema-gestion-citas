package com.proyecto.gestioncitas.notificacion.domain.enums;

/**
 * Medio por el que se entrega el aviso.
 *
 * Cada canal tiene su implementacion de NotificacionSender, y el servicio que
 * envia elige por este valor sin preguntar de que canal se trata.
 */
public enum CanalNotificacion {

    /** Correo electronico. Llega aunque el paciente no entre al sistema. */
    EMAIL,

    /** Aviso dentro de la aplicacion, en la campana de notificaciones. */
    IN_APP
}
