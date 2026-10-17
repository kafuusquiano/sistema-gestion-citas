package com.proyecto.gestioncitas.cita.domain.enums;

/**
 * Naturaleza de la atencion.
 *
 * EMERGENCIA se salta la validacion de plazo minimo de cancelacion, porque no
 * tiene sentido exigir veinticuatro horas de anticipacion en un caso urgente.
 */
public enum TipoCita {

    /** Primera atencion por un motivo nuevo. */
    CONSULTA,

    /** Seguimiento de una atencion anterior. */
    CONTROL,

    /** Atencion urgente. No admite reserva anticipada por el paciente. */
    EMERGENCIA;

    /** Solo admision puede registrar una emergencia, nunca el propio paciente. */
    public boolean requierePersonalAdmision() {
        return this == EMERGENCIA;
    }
}
