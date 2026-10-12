package com.proyecto.gestioncitas.medico.domain.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class EspecialidadResponse {
    private final String id;
    private final String nombre;
    private final String descripcion;
    private final boolean activa;
}
