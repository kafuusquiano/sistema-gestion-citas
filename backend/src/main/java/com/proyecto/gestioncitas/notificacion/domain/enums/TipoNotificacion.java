package com.proyecto.gestioncitas.notificacion.domain.enums;

/**
 * Motivo por el que se avisa al paciente.
 *
 * Cada tipo determina que plantilla de correo se usa y que asunto lleva, de
 * modo que agregar un aviso nuevo es agregar una constante aqui y su plantilla,
 * sin tocar el servicio que los envia.
 */
public enum TipoNotificacion {

    BIENVENIDA("bienvenida", "Bienvenido al Centro de Salud Acapulco"),
    CITA_CONFIRMADA("cita-confirmada", "Su cita medica ha sido registrada"),
    CITA_CANCELADA("cita-cancelada", "Su cita medica fue cancelada"),
    CITA_REPROGRAMADA("cita-reprogramada", "Su cita medica cambio de fecha"),
    RECORDATORIO("recordatorio-cita", "Recordatorio: tiene una cita manana");

    private final String plantilla;
    private final String asunto;

    TipoNotificacion(String plantilla, String asunto) {
        this.plantilla = plantilla;
        this.asunto = asunto;
    }

    /** Nombre del archivo en templates/email, sin la extension. */
    public String getPlantilla() {
        return plantilla;
    }

    public String getAsunto() {
        return asunto;
    }
}
