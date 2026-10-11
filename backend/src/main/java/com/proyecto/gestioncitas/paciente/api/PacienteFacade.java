package com.proyecto.gestioncitas.paciente.api;

import com.proyecto.gestioncitas.paciente.domain.dto.response.PacienteResponse;
import java.util.Optional;

public interface PacienteFacade {
    Optional<PacienteResponse> obtenerPorId(String id);
    boolean existePaciente(String id);
}
