package com.proyecto.gestioncitas.cita.api;

import java.time.Instant;

/**
 * Puerta de entrada al modulo cita desde otros modulos (instruccion M1).
 *
 * Hoy la consume notificacion para componer el recordatorio. Se mantiene
 * minima a proposito: cuanto menos expone un modulo, menos ata a los demas.
 */
public interface CitaFacade {

    boolean existe(Long citaId);

    /**
     * @throws com.proyecto.gestioncitas.common.exception.ResourceNotFoundException
     *         si la cita no existe
     */
    ResumenCita obtenerResumen(Long citaId);

    /** Citas vigentes de un paciente, para avisarle de lo que tiene pendiente. */
    long contarVigentesDelPaciente(Long pacienteId);

    /** Lo que otro modulo puede ver de una cita. */
    record ResumenCita(
            Long id,
            Long pacienteId,
            Long medicoId,
            Instant fechaHora,
            String estado,
            boolean vigente) {
    }
}
