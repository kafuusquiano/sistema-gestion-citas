package com.proyecto.gestioncitas.medico.domain.enums;

/**
 * Discriminador de la jerarquia Medico.
 *
 * Es el valor que guarda la columna tipo_medico de V3__medico.sql y el que
 * decide si Hibernate instancia un MedicoGeneral o un MedicoEspecialista al
 * leer una fila.
 */
public enum TipoMedico {

    /** Medico general. Atiende consulta ambulatoria y no tiene RNE. */
    GENERAL,

    /**
     * Medico especialista. Tiene Registro Nacional de Especialista, que la
     * restriccion ck_medicos_rne_por_tipo exige a nivel de base de datos.
     */
    ESPECIALISTA
}
