package com.proyecto.gestioncitas.medico.domain.dto.request;

import com.proyecto.gestioncitas.medico.domain.enums.EstadoMedico;
import lombok.Data;

@Data
public class ActualizarMedicoRequest {
    private String telefono;
    private String especialidadId;
    private EstadoMedico estado;
}
