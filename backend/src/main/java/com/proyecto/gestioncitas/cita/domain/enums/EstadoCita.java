package com.proyecto.gestioncitas.cita.domain.enums;

import java.util.Set;

/**
 * Estados por los que pasa una cita.
 *
 * Cubre el objetivo especifico de seguimiento y control: el estado dice si la
 * cita esta programada, fue atendida, se cancelo o el paciente no asistio.
 *
 * Cada constante declara a que estados puede pasar. Tener la regla aqui y no
 * en un if disperso por los servicios evita que una transicion se valide en un
 * sitio y se olvide en otro.
 */
public enum EstadoCita {

    /** Recien agendada. Falta que el paciente o admision la confirme. */
    PROGRAMADA,

    /** Confirmada por el paciente. Sigue ocupando el cupo. */
    CONFIRMADA,

    /** El paciente fue atendido. Estado final. */
    COMPLETADA,

    /** Anulada antes de la hora. Libera el cupo. Estado final. */
    CANCELADA,

    /** Marca de historial para una reprogramacion. No es un estado de reposo. */
    REPROGRAMADA,

    /** El paciente no se presento. Estado final que alimenta el control de ausentismo. */
    NO_ASISTIO;

    static {
        PROGRAMADA.siguientes = Set.of(CONFIRMADA, CANCELADA, COMPLETADA, NO_ASISTIO);
        CONFIRMADA.siguientes = Set.of(COMPLETADA, CANCELADA, NO_ASISTIO);
        COMPLETADA.siguientes = Set.of();
        CANCELADA.siguientes = Set.of();
        REPROGRAMADA.siguientes = Set.of();
        NO_ASISTIO.siguientes = Set.of();
    }

    private Set<EstadoCita> siguientes;

    public boolean puedePasarA(EstadoCita destino) {
        return siguientes.contains(destino);
    }

    public Set<EstadoCita> transicionesPosibles() {
        return siguientes;
    }

    /**
     * Un estado final ya no admite cambios. Es tambien el que libera el cupo:
     * el indice unico parcial de la tabla solo considera PROGRAMADA y
     * CONFIRMADA.
     */
    public boolean esFinal() {
        return siguientes.isEmpty();
    }

    /** true si la cita todavia ocupa el turno del medico. */
    public boolean ocupaCupo() {
        return this == PROGRAMADA || this == CONFIRMADA;
    }
}
