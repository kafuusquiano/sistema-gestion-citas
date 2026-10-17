package com.proyecto.gestioncitas.medico.domain.dto.response;

import com.proyecto.gestioncitas.medico.domain.enums.EstadoMedico;
import com.proyecto.gestioncitas.medico.domain.enums.TipoMedico;
import java.time.Instant;
import java.util.List;

/**
 * Ficha completa del medico.
 *
 * Los campos descripcionProfesional, duracionConsultaMinutos y
 * recibeDerivaciones vienen de los metodos polimorficos de la jerarquia: cada
 * subclase responde lo suyo y el DTO solo transporta el resultado. El
 * frontend no necesita saber que subclase es para mostrarlos.
 *
 * rne y aniosExperiencia solo llegan en un ESPECIALISTA; areaRotacion solo en
 * un GENERAL. En el otro caso van nulos y Jackson los omite, porque
 * application.yml fija default-property-inclusion: non_null.
 */
public record MedicoResponse(
        Long id,
        Long usuarioId,
        TipoMedico tipo,
        String cmp,
        String nombres,
        String apellidoPaterno,
        String apellidoMaterno,
        String nombreCompleto,
        String telefono,
        EspecialidadResponse especialidad,
        EstadoMedico estado,

        /** "Medico General" o "Especialista en Obstetricia". */
        String descripcionProfesional,

        /** Cabecera de receta: "Medico General Juan Perez - CMP 12345". */
        String firmaProfesional,

        int duracionConsultaMinutos,
        boolean recibeDerivaciones,

        // Solo ESPECIALISTA
        String rne,
        Short aniosExperiencia,

        // Solo GENERAL
        String areaRotacion,

        List<HorarioResponse> horarios,
        Instant createdAt) {
}
