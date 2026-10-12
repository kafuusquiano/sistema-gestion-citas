package com.proyecto.gestioncitas.medico.api;

import com.proyecto.gestioncitas.medico.domain.dto.response.MedicoResponse;
import com.proyecto.gestioncitas.medico.domain.dto.response.HorarioResponse;
import java.util.List;
import java.util.Optional;

public interface MedicoFacade {
    Optional<MedicoResponse> obtenerPorId(String id);
    boolean existeMedico(String id);
    List<HorarioResponse> obtenerHorarios(String medicoId);
}
