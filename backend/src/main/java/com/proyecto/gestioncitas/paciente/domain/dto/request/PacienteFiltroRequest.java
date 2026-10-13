package com.proyecto.gestioncitas.paciente.domain.dto.request;

import com.proyecto.gestioncitas.paciente.domain.enums.EstadoPaciente;
import com.proyecto.gestioncitas.paciente.domain.enums.Seguro;
import com.proyecto.gestioncitas.paciente.domain.enums.Sexo;

/**
 * Filtros de GET /api/pacientes. Todos opcionales: un campo nulo no filtra.
 *
 * PacienteSpecification los traduce a predicados. El campo texto es el que
 * sirve al objetivo especifico 1: una sola caja de busqueda que cruza
 * documento, nombres y numero de historia, para que admision no tenga que
 * elegir primero por que campo buscar.
 */
public record PacienteFiltroRequest(

        /** Busca en documento, nombres, apellidos y numero de historia. */
        String texto,

        EstadoPaciente estado,

        Sexo sexo,

        Seguro seguro,

        String distrito,

        /** Rango de edad en anos cumplidos. */
        Integer edadMinima,

        Integer edadMaxima,

        /** true devuelve solo los que ya tienen cuenta de acceso. */
        Boolean conCuenta) {

    /** Filtro vacio, para el listado sin criterios. */
    public static PacienteFiltroRequest vacio() {
        return new PacienteFiltroRequest(null, null, null, null, null, null, null, null);
    }
}
