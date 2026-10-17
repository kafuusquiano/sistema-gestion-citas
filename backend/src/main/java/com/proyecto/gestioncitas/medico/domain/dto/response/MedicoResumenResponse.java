package com.proyecto.gestioncitas.medico.domain.dto.response;

import com.proyecto.gestioncitas.medico.domain.enums.TipoMedico;

/**
 * Version corta, para los listados y el selector de medico al agendar.
 *
 * Es lo que ve el paciente al elegir con quien atenderse, de ahi que incluya
 * descripcionProfesional y no el tipo en crudo.
 */
public record MedicoResumenResponse(
        Long id,
        TipoMedico tipo,
        String cmp,
        String nombreCompleto,
        String especialidad,
        String descripcionProfesional,
        int duracionConsultaMinutos,
        boolean admiteCitas) {
}
