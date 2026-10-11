package com.proyecto.gestioncitas.paciente.api;

import com.proyecto.gestioncitas.paciente.domain.dto.response.PacienteResponse;
import com.proyecto.gestioncitas.paciente.mapper.PacienteMapper;
import com.proyecto.gestioncitas.paciente.repository.PacienteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class PacienteFacadeImpl implements PacienteFacade {
    private final PacienteRepository pacienteRepository;
    private final PacienteMapper     pacienteMapper;

    @Override
    public Optional<PacienteResponse> obtenerPorId(String id) {
        return pacienteRepository.findById(id).map(pacienteMapper::toResponse);
    }

    @Override
    public boolean existePaciente(String id) {
        return pacienteRepository.existsById(id);
    }
}
