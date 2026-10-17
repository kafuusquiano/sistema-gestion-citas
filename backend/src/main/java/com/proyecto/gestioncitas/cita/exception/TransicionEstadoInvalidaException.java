package com.proyecto.gestioncitas.cita.exception;

import com.proyecto.gestioncitas.cita.domain.enums.EstadoCita;
import com.proyecto.gestioncitas.common.exception.BusinessException;
import com.proyecto.gestioncitas.common.exception.ErrorCode;
import java.util.stream.Collectors;

/**
 * Se pidio un cambio de estado que las reglas no permiten, por ejemplo
 * confirmar una cita ya cancelada. Se traduce a 409.
 *
 * El mensaje enumera los estados a los que si se puede pasar, para que quien
 * use la API sepa que hacer en lugar de adivinar.
 */
public class TransicionEstadoInvalidaException extends BusinessException {

    public TransicionEstadoInvalidaException(EstadoCita actual, EstadoCita destino) {
        super(ErrorCode.TRANSICION_ESTADO_INVALIDA, construirMensaje(actual, destino));
    }

    private static String construirMensaje(EstadoCita actual, EstadoCita destino) {
        if (actual.esFinal()) {
            return "La cita esta %s, que es un estado final, y ya no admite cambios"
                    .formatted(actual);
        }
        String posibles = actual.transicionesPosibles().stream()
                .map(Enum::name)
                .collect(Collectors.joining(", "));
        return "Una cita %s no puede pasar a %s. Transiciones validas: %s"
                .formatted(actual, destino, posibles);
    }
}
