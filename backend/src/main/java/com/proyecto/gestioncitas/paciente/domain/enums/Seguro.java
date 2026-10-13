package com.proyecto.gestioncitas.paciente.domain.enums;

/**
 * Cobertura del paciente.
 *
 * Es dato obligatorio en un establecimiento de la DIRESA: condiciona la
 * gratuidad de la atencion y es la variable principal de los informes de
 * produccion que el Centro de Salud rinde a la Direccion Regional.
 */
public enum Seguro {

    /** Seguro Integral de Salud, el del Estado. Es el caso mayoritario. */
    SIS,

    ESSALUD,

    /** Seguro privado o pago de bolsillo. */
    PRIVADO,

    NINGUNO
}
