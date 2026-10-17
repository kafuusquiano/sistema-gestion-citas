package com.proyecto.gestioncitas.medico.domain.enums;

/**
 * Por que un medico no atiende en un rango de fechas.
 *
 * Corresponde al CHECK ck_excepciones_motivo de V3__medico.sql. Al calcular
 * la disponibilidad, estas fechas se restan de la plantilla semanal: es lo
 * que evita agendar una cita un dia que el medico no esta.
 */
public enum MotivoExcepcion {

    VACACIONES,

    /** Feriado del establecimiento. Suele aplicar a todos los medicos. */
    FERIADO,

    LICENCIA,

    CAPACITACION,

    OTRO
}
