package com.proyecto.gestioncitas.paciente.domain.enums;

/**
 * Sexo del paciente segun el documento de identidad.
 *
 * Son solo dos valores porque es el dato biologico que condiciona los rangos
 * de referencia de laboratorio: la hemoglobina normal de un varon no es la de
 * una mujer, y RangoReferenciaEvaluator elige la fila por este campo.
 */
public enum Sexo {
    MASCULINO,
    FEMENINO
}
