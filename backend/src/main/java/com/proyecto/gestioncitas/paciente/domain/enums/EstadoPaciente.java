package com.proyecto.gestioncitas.paciente.domain.enums;

/**
 * Estado de la ficha del paciente.
 *
 * Un paciente nunca se borra: su historial clinico tiene que quedar para la
 * trazabilidad que pide el objetivo especifico 3. Darlo de baja lo pasa a
 * INACTIVO, que lo saca de los listados y le impide agendar citas nuevas sin
 * perder un solo dato.
 */
public enum EstadoPaciente {

    ACTIVO,
    INACTIVO;

    public boolean puedeAgendar() {
        return this == ACTIVO;
    }
}
