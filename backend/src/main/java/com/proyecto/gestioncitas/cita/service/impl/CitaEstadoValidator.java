package com.proyecto.gestioncitas.cita.service.impl;

import com.proyecto.gestioncitas.cita.domain.entity.Cita;
import com.proyecto.gestioncitas.cita.domain.enums.EstadoCita;
import com.proyecto.gestioncitas.cita.exception.TransicionEstadoInvalidaException;
import com.proyecto.gestioncitas.common.constants.AppConstants;
import com.proyecto.gestioncitas.common.exception.BusinessException;
import com.proyecto.gestioncitas.common.exception.ErrorCode;
import org.springframework.stereotype.Component;

/**
 * Guardian de las reglas de transicion entre estados.
 *
 * Existe aparte del servicio porque estas reglas se consultan desde varios
 * metodos y porque conviene poder probarlas sin levantar la base de datos.
 * La entidad Cita aplica el efecto; esta clase decide si el efecto procede.
 */
@Component
public class CitaEstadoValidator {

    /**
     * @throws TransicionEstadoInvalidaException si el cambio no esta permitido
     */
    public void validarTransicion(Cita cita, EstadoCita destino) {
        if (!cita.getEstado().puedePasarA(destino)) {
            throw new TransicionEstadoInvalidaException(cita.getEstado(), destino);
        }
    }

    /**
     * Reglas de la cancelacion.
     *
     * El plazo minimo protege la agenda del establecimiento: cancelar media
     * hora antes deja un cupo que ya nadie alcanza a tomar. Una emergencia
     * queda exenta, porque exigir anticipacion ahi no tiene sentido.
     *
     * @param esPersonal true si quien cancela es ADMIN o MEDICO, que no estan
     *        sujetos al plazo porque cancelan por motivos del servicio
     */
    public void validarCancelacion(Cita cita, boolean esPersonal) {
        validarTransicion(cita, EstadoCita.CANCELADA);

        if (esPersonal || cita.getTipo().requierePersonalAdmision()) {
            return;
        }

        long horas = cita.horasHastaLaCita();
        if (horas < AppConstants.HORAS_MINIMAS_CANCELACION) {
            throw new BusinessException(ErrorCode.CANCELACION_FUERA_DE_PLAZO,
                    "La cita solo puede cancelarse con al menos %d horas de anticipacion. Faltan %d."
                            .formatted(AppConstants.HORAS_MINIMAS_CANCELACION, Math.max(0, horas)));
        }
    }

    /**
     * Una cita no puede completarse antes de que llegue su hora: marcarla como
     * atendida por adelantado falsearia el registro de produccion.
     */
    public void validarCompletar(Cita cita) {
        validarTransicion(cita, EstadoCita.COMPLETADA);

        if (!cita.yaPaso()) {
            throw new BusinessException(ErrorCode.CONFLICTO_DE_ESTADO,
                    "No se puede completar una cita cuya hora todavia no llega");
        }
    }

    /** Reprogramar equivale a mover la cita, asi que solo vale si esta vigente. */
    public void validarReprogramacion(Cita cita) {
        if (!cita.estaVigente()) {
            throw new TransicionEstadoInvalidaException(
                    cita.getEstado(), EstadoCita.REPROGRAMADA);
        }
    }
}
