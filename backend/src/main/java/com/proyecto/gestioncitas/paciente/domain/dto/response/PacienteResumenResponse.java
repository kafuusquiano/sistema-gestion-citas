package com.proyecto.gestioncitas.paciente.domain.dto.response;

import com.proyecto.gestioncitas.paciente.domain.enums.Sexo;

/**
 * Version corta de la ficha, para las tablas y los selectores.
 *
 * Existe aparte de PacienteResponse por el peso: el listado de admision
 * devuelve 20 pacientes por pagina, y mandar la ficha completa con los
 * contactos de emergencia en cada fila seria enviar datos que la tabla no
 * muestra. Es tambien lo que PacienteFacade entrega a otros modulos.
 */
public record PacienteResumenResponse(
        Long id,
        String numeroHistoria,
        String numeroDocumento,
        String nombreCompleto,
        int edad,
        Sexo sexo,
        String telefono,
        boolean activo) {
}
