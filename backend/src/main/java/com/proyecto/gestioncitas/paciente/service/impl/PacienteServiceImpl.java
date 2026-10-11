package com.proyecto.gestioncitas.paciente.service.impl;

import com.proyecto.gestioncitas.common.dto.PageResponse;
import com.proyecto.gestioncitas.common.exception.*;
import com.proyecto.gestioncitas.paciente.domain.dto.request.*;
import com.proyecto.gestioncitas.paciente.domain.dto.response.PacienteResponse;
import com.proyecto.gestioncitas.paciente.domain.entity.Paciente;
import com.proyecto.gestioncitas.paciente.mapper.PacienteMapper;
import com.proyecto.gestioncitas.paciente.repository.PacienteRepository;
import com.proyecto.gestioncitas.paciente.service.PacienteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PacienteServiceImpl implements PacienteService {

    private final PacienteRepository pacienteRepository;
    private final PacienteMapper     pacienteMapper;

    @Override
    @Transactional
    public PacienteResponse crear(CrearPacienteRequest req) {
        if (pacienteRepository.existsByDni(req.getDni()))
            throw new BusinessException("DNI_DUPLICADO", "Ya existe un paciente con ese DNI");
        if (pacienteRepository.existsByEmail(req.getEmail()))
            throw new BusinessException("EMAIL_DUPLICADO", "Ya existe un paciente con ese email");

        Paciente p = Paciente.builder()
            .id(req.getUsuarioId())
            .usuarioId(req.getUsuarioId())
            .nombre(req.getNombre())
            .apellido(req.getApellido())
            .email(req.getEmail())
            .dni(req.getDni())
            .fechaNacimiento(req.getFechaNacimiento())
            .sexo(req.getSexo())
            .telefono(req.getTelefono())
            .direccion(req.getDireccion())
            .tipoSangre(req.getTipoSangre())
            .alergias(req.getAlergias())
            .build();

        return pacienteMapper.toResponse(pacienteRepository.save(p));
    }

    @Override
    public PacienteResponse obtenerPorId(String id) {
        return pacienteMapper.toResponse(pacienteRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Paciente", "id", id)));
    }

    @Override
    public PacienteResponse obtenerPorUsuarioId(String usuarioId) {
        return pacienteMapper.toResponse(pacienteRepository.findByUsuarioId(usuarioId)
            .orElseThrow(() -> new ResourceNotFoundException("Paciente", "usuarioId", usuarioId)));
    }

    @Override
    public PageResponse<PacienteResponse> listar(int page, int size) {
        Page<PacienteResponse> p = pacienteRepository
            .findAll(PageRequest.of(page, size))
            .map(pacienteMapper::toResponse);
        return PageResponse.<PacienteResponse>builder()
            .content(p.getContent()).page(p.getNumber())
            .size(p.getSize()).totalElements(p.getTotalElements())
            .totalPages(p.getTotalPages()).last(p.isLast()).build();
    }

    @Override
    @Transactional
    public PacienteResponse actualizar(String id, ActualizarPacienteRequest req) {
        Paciente p = pacienteRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Paciente", "id", id));
        if (req.getTelefono()       != null) p.setTelefono(req.getTelefono());
        if (req.getDireccion()      != null) p.setDireccion(req.getDireccion());
        if (req.getFechaNacimiento()!= null) p.setFechaNacimiento(req.getFechaNacimiento());
        if (req.getSexo()           != null) p.setSexo(req.getSexo());
        if (req.getTipoSangre()     != null) p.setTipoSangre(req.getTipoSangre());
        if (req.getAlergias()       != null) p.setAlergias(req.getAlergias());
        return pacienteMapper.toResponse(pacienteRepository.save(p));
    }

    @Override
    @Transactional
    public void desactivar(String id) {
        Paciente p = pacienteRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Paciente", "id", id));
        p.setActivo(false);
        pacienteRepository.save(p);
        log.info("Paciente desactivado: {}", id);
    }
}
