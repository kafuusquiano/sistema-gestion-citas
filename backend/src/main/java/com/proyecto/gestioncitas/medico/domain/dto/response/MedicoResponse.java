package com.proyecto.gestioncitas.medico.domain.dto.response;

import com.proyecto.gestioncitas.medico.domain.enums.EstadoMedico;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MedicoResponse {
    private final String id;
    private final String usuarioId;
    private final String nombre;
    private final String apellido;
    private final String email;
    private final String dni;
    private final String codigoCmp;
    private final String telefono;
    private final String especialidadId;
    private final String especialidadNombre;
    private final EstadoMedico estado;
}
