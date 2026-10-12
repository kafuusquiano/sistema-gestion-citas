package com.proyecto.gestioncitas.medico.api;

import com.proyecto.gestioncitas.medico.domain.dto.response.HorarioResponse;
import com.proyecto.gestioncitas.medico.domain.dto.response.MedicoResponse;
import com.proyecto.gestioncitas.medico.mapper.MedicoMapper;
import com.proyecto.gestioncitas.medico.repository.HorarioMedicoRepository;
import com.proyecto.gestioncitas.medico.repository.MedicoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class MedicoFacadeImpl implements MedicoFacade {
    private final MedicoRepository       medicoRepository;
    private final HorarioMedicoRepository horarioRepository;
    private final MedicoMapper            medicoMapper;

    @Override
    public Optional<MedicoResponse> obtenerPorId(String id) {
        return medicoRepository.findById(id).map(medicoMapper::toResponse);
    }

    @Override
    public boolean existeMedico(String id) {
        return medicoRepository.existsById(id);
    }

    @Override
    public List<HorarioResponse> obtenerHorarios(String medicoId) {
        return horarioRepository.findByMedicoIdAndActivoTrue(medicoId)
            .stream().map(medicoMapper::toHorarioResponse).toList();
    }
}
