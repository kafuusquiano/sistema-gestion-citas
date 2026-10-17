package com.proyecto.gestioncitas.medico.domain.enums;

/**
 * Situacion del medico en el establecimiento.
 *
 * LICENCIA es distinto de INACTIVO: el medico sigue siendo parte del personal
 * y conserva sus citas ya agendadas, pero no acepta nuevas. INACTIVO es la
 * baja definitiva.
 */
public enum EstadoMedico {

    ACTIVO,

    /** De licencia o vacaciones prolongadas. No admite citas nuevas. */
    LICENCIA,

    INACTIVO;

    /** Solo un medico activo aparece en la busqueda de disponibilidad. */
    public boolean admiteCitas() {
        return this == ACTIVO;
    }
}
