package com.proyecto.gestioncitas.medico.domain.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Cuerpo de PUT /api/medicos/{id}.
 *
 * No incluye el CMP ni el tipo de medico. El CMP es inmutable, y cambiar el
 * tipo significaria cambiar la clase de la fila: en SINGLE_TABLE eso obliga a
 * reescribir el discriminador, que JPA no permite. Si un general se titula
 * como especialista, se da de baja su registro y se crea el nuevo.
 */
public record ActualizarMedicoRequest(

        @NotBlank(message = "Los nombres son obligatorios")
        @Size(max = 100)
        String nombres,

        @NotBlank(message = "El apellido paterno es obligatorio")
        @Size(max = 60)
        String apellidoPaterno,

        @Size(max = 60)
        String apellidoMaterno,

        @Size(max = 20)
        String telefono,

        @NotNull(message = "La especialidad es obligatoria")
        Long especialidadId,

        /** Solo aplica a un ESPECIALISTA; en un GENERAL se ignora. */
        @Min(0) @Max(70)
        Short aniosExperiencia,

        /** Solo aplica a un GENERAL; en un ESPECIALISTA se ignora. */
        @Size(max = 80)
        String areaRotacion) {
}
