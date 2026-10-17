package com.proyecto.gestioncitas.notificacion.domain.enums;

/**
 * Situacion del aviso.
 *
 * FALLIDA no es un estado final: el proceso de reintento vuelve a tomar las
 * notificaciones en ese estado, porque un fallo del servidor de correo suele
 * ser temporal.
 */
public enum EstadoNotificacion {

    PENDIENTE,
    ENVIADA,
    FALLIDA,
    LEIDA;

    /** PENDIENTE y FALLIDA entran en el proximo intento de envio. */
    public boolean admiteReintento() {
        return this == PENDIENTE || this == FALLIDA;
    }

    public boolean fueEntregada() {
        return this == ENVIADA || this == LEIDA;
    }
}
