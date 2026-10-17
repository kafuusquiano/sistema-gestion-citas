package com.proyecto.gestioncitas.medico.domain.dto.response;

public record EspecialidadResponse(
        Long id,
        String nombre,
        String descripcion,
        boolean activo) {
}
