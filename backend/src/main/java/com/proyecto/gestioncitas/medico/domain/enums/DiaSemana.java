package com.proyecto.gestioncitas.medico.domain.enums;

import java.time.DayOfWeek;

/**
 * Dia de la semana del horario de atencion.
 *
 * Existe en lugar de usar java.time.DayOfWeek directamente porque la columna
 * dia_semana de V3__medico.sql guarda el nombre en espanol, que es lo que el
 * personal lee en la agenda. El puente con DayOfWeek esta en los dos metodos
 * de conversion, que son los que usa DisponibilidadService para saber que
 * franja aplica a una fecha concreta.
 */
public enum DiaSemana {

    LUNES(DayOfWeek.MONDAY),
    MARTES(DayOfWeek.TUESDAY),
    MIERCOLES(DayOfWeek.WEDNESDAY),
    JUEVES(DayOfWeek.THURSDAY),
    VIERNES(DayOfWeek.FRIDAY),
    SABADO(DayOfWeek.SATURDAY),
    DOMINGO(DayOfWeek.SUNDAY);

    private final DayOfWeek equivalente;

    DiaSemana(DayOfWeek equivalente) {
        this.equivalente = equivalente;
    }

    public DayOfWeek aDayOfWeek() {
        return equivalente;
    }

    /** Dia de la semana de una fecha concreta. */
    public static DiaSemana desde(DayOfWeek dia) {
        for (DiaSemana d : values()) {
            if (d.equivalente == dia) {
                return d;
            }
        }
        throw new IllegalArgumentException("Dia no reconocido: " + dia);
    }

    public static DiaSemana desde(java.time.LocalDate fecha) {
        return desde(fecha.getDayOfWeek());
    }

    public boolean esFinDeSemana() {
        return this == SABADO || this == DOMINGO;
    }
}
