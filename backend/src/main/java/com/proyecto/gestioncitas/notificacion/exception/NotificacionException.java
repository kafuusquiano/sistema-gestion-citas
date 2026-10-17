package com.proyecto.gestioncitas.notificacion.exception;

import com.proyecto.gestioncitas.common.exception.BusinessException;
import com.proyecto.gestioncitas.common.exception.ErrorCode;

/**
 * La entrega de un aviso fallo.
 *
 * No se propaga al usuario que origino la accion: si un correo no sale, la
 * cita igual quedo agendada. El servicio la captura, la registra en la
 * notificacion y deja que el proceso de reintento lo vuelva a intentar.
 */
public class NotificacionException extends BusinessException {

    public NotificacionException(String mensaje) {
        super(ErrorCode.ENVIO_NOTIFICACION_FALLIDO, mensaje);
    }

    public NotificacionException(String mensaje, Throwable causa) {
        super(ErrorCode.ENVIO_NOTIFICACION_FALLIDO, mensaje, causa);
    }
}
