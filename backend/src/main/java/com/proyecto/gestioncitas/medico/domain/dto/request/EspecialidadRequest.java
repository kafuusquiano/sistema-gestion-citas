package com.proyecto.gestioncitas.medico.domain.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Cuerpo de POST y PUT /api/especialidades. Solo ADMIN.
 */
public record EspecialidadRequest(

        @NotBlank(message = "El nombre de la especialidad es obligatorio")
        @Size(max = 100)
        String nombre,

        @Size(max = 300)
        String descripcion) {
}
