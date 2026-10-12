package com.proyecto.gestioncitas.medico.service.impl;

import com.proyecto.gestioncitas.common.exception.BusinessException;
import com.proyecto.gestioncitas.medico.domain.dto.response.EspecialidadResponse;
import com.proyecto.gestioncitas.medico.domain.entity.Especialidad;
import com.proyecto.gestioncitas.medico.mapper.MedicoMapper;
import com.proyecto.gestioncitas.medico.repository.EspecialidadRepository;
import com.proyecto.gestioncitas.medico.service.EspecialidadService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EspecialidadServiceImpl implements EspecialidadService {
    private final EspecialidadRepository especialidadRepository;
    private final MedicoMapper           medicoMapper;

    @Override
    public List<EspecialidadResponse> listarActivas() {
        return especialidadRepository.findByActivaTrue()
            .stream().map(medicoMapper::toEspecialidadResponse).toList();
    }

    @Override
    @Transactional
    public EspecialidadResponse crear(String nombre, String descripcion) {
        if (especialidadRepository.existsByNombre(nombre))
            throw new BusinessException("ESPECIALIDAD_DUPLICADA",
                "Ya existe una especialidad con ese nombre");
        return medicoMapper.toEspecialidadResponse(
            especialidadRepository.save(Especialidad.builder()
                .nombre(nombre).descripcion(descripcion).build()));
    }
}
