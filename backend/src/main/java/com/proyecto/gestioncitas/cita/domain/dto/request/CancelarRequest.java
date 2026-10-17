package com.proyecto.gestioncitas.cita.domain.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Cuerpo de PATCH /api/citas/{id}/cancelar.
 *
 * El motivo es obligatorio porque la restriccion ck_citas_motivo_cancelacion
 * de la base lo exige, y porque alimenta el analisis de por que se cancelan
 * las citas en el establecimiento.
 */
public record CancelarRequest(

        @NotBlank(message = "El motivo de cancelacion es obligatorio")
        @Size(max = 300)
        String motivo) {
}
