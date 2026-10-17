package com.proyecto.gestioncitas.cita.exception;

import com.proyecto.gestioncitas.common.exception.BusinessException;
import com.proyecto.gestioncitas.common.exception.ErrorCode;
import java.time.LocalDateTime;

/**
 * El turno pedido ya no esta libre. Se traduce a 409.
 *
 * Se lanza en dos momentos: al validar antes de guardar, y al capturar el
 * rechazo del indice unico uk_citas_medico_slot si dos operadores reservaron
 * el mismo cupo a la vez. El segundo caso es el que la validacion previa no
 * puede evitar.
 */
public class SlotNoDisponibleException extends BusinessException {

    public SlotNoDisponibleException() {
        super(ErrorCode.SLOT_NO_DISPONIBLE);
    }

    public SlotNoDisponibleException(LocalDateTime fechaHora) {
        super(ErrorCode.SLOT_NO_DISPONIBLE,
                "El horario de las %s ya fue reservado".formatted(fechaHora.toLocalTime()));
    }

    public static SlotNoDisponibleException fueraDeHorario(LocalDateTime fechaHora) {
        return new SlotNoDisponibleException(
                "El medico no atiende el %s a las %s".formatted(
                        fechaHora.toLocalDate(), fechaHora.toLocalTime()));
    }

    private SlotNoDisponibleException(String mensaje) {
        super(ErrorCode.FUERA_DE_HORARIO_ATENCION, mensaje);
    }
}
