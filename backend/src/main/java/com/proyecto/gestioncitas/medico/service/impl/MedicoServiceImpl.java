package com.proyecto.gestioncitas.medico.service.impl;

import com.proyecto.gestioncitas.common.dto.PageResponse;
import com.proyecto.gestioncitas.common.event.EventPublisher;
import com.proyecto.gestioncitas.common.exception.*;
import com.proyecto.gestioncitas.medico.domain.dto.request.*;
import com.proyecto.gestioncitas.medico.domain.dto.response.*;
import com.proyecto.gestioncitas.medico.domain.entity.*;
import com.proyecto.gestioncitas.medico.domain.enums.EstadoMedico;
import com.proyecto.gestioncitas.medico.event.MedicoHorarioActualizadoEvent;
import com.proyecto.gestioncitas.medico.mapper.MedicoMapper;
import com.proyecto.gestioncitas.medico.repository.*;
import com.proyecto.gestioncitas.medico.service.MedicoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class MedicoServiceImpl implements MedicoService {

    private final MedicoRepository        medicoRepository;
    private final EspecialidadRepository  especialidadRepository;
    private final HorarioMedicoRepository horarioRepository;
    private final MedicoMapper            medicoMapper;
    private final EventPublisher          eventPublisher;

    @Override
    @Transactional
    public MedicoResponse crear(CrearMedicoRequest req) {
        if (medicoRepository.existsByDni(req.getDni()))
            throw new BusinessException("DNI_DUPLICADO", "Ya existe un médico con ese DNI");
        if (medicoRepository.existsByEmail(req.getEmail()))
            throw new BusinessException("EMAIL_DUPLICADO", "Ya existe un médico con ese email");

        Especialidad esp = especialidadRepository.findById(req.getEspecialidadId())
            .orElseThrow(() -> new ResourceNotFoundException("Especialidad", "id", req.getEspecialidadId()));

        Medico m = Medico.builder()
            .id(req.getUsuarioId())
            .usuarioId(req.getUsuarioId())
            .nombre(req.getNombre())
            .apellido(req.getApellido())
            .email(req.getEmail())
            .dni(req.getDni())
            .codigoCmp(req.getCodigoCmp())
            .telefono(req.getTelefono())
            .especialidad(esp)
            .build();

        return medicoMapper.toResponse(medicoRepository.save(m));
    }

    @Override
    public MedicoResponse obtenerPorId(String id) {
        return medicoMapper.toResponse(medicoRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Médico", "id", id)));
    }

    @Override
    public PageResponse<MedicoResponse> listar(int page, int size) {
        Page<MedicoResponse> p = medicoRepository
            .findAll(PageRequest.of(page, size))
            .map(medicoMapper::toResponse);
        return PageResponse.<MedicoResponse>builder()
            .content(p.getContent()).page(p.getNumber())
            .size(p.getSize()).totalElements(p.getTotalElements())
            .totalPages(p.getTotalPages()).last(p.isLast()).build();
    }

    @Override
    public List<MedicoResponse> listarPorEspecialidad(String especialidadId) {
        return medicoRepository
            .findByEspecialidadIdAndEstado(especialidadId, EstadoMedico.ACTIVO)
            .stream().map(medicoMapper::toResponse).toList();
    }

    @Override
    @Transactional
    public MedicoResponse actualizar(String id, ActualizarMedicoRequest req) {
        Medico m = medicoRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Médico", "id", id));
        if (req.getTelefono()      != null) m.setTelefono(req.getTelefono());
        if (req.getEstado()        != null) m.setEstado(req.getEstado());
        if (req.getEspecialidadId()!= null) {
            Especialidad esp = especialidadRepository.findById(req.getEspecialidadId())
                .orElseThrow(() -> new ResourceNotFoundException("Especialidad", "id", req.getEspecialidadId()));
            m.setEspecialidad(esp);
        }
        return medicoMapper.toResponse(medicoRepository.save(m));
    }

    @Override
    @Transactional
    @CacheEvict(value = "horario-medico", key = "#medicoId")
    public HorarioResponse agregarHorario(String medicoId, HorarioRequest req) {
        Medico medico = medicoRepository.findById(medicoId)
            .orElseThrow(() -> new ResourceNotFoundException("Médico", "id", medicoId));

        if (horarioRepository.existsByMedicoIdAndDiaSemana(medicoId, req.getDiaSemana()))
            throw new BusinessException("HORARIO_DUPLICADO",
                "Ya existe un horario para ese día");

        HorarioMedico h = HorarioMedico.builder()
            .medico(medico)
            .diaSemana(req.getDiaSemana())
            .horaInicio(req.getHoraInicio())
            .horaFin(req.getHoraFin())
            .duracionCitaMin(req.getDuracionCitaMin())
            .build();

        HorarioMedico saved = horarioRepository.save(h);
        eventPublisher.publish(new MedicoHorarioActualizadoEvent(medicoId));
        return medicoMapper.toHorarioResponse(saved);
    }

    @Override
    @Cacheable(value = "horario-medico", key = "#medicoId")
    public List<HorarioResponse> obtenerHorarios(String medicoId) {
        return horarioRepository.findByMedicoIdAndActivoTrue(medicoId)
            .stream().map(medicoMapper::toHorarioResponse).toList();
    }

    @Override
    public List<SlotDisponibleResponse> obtenerSlotsDisponibles(String medicoId, LocalDate fecha) {
        var horarios = horarioRepository.findByMedicoIdAndActivoTrue(medicoId);
        List<SlotDisponibleResponse> slots = new ArrayList<>();
        horarios.stream()
            .filter(h -> h.getDiaSemana().name().equals(fecha.getDayOfWeek().name()))
            .findFirst()
            .ifPresent(h -> {
                LocalTime actual = h.getHoraInicio();
                while (actual.plusMinutes(h.getDuracionCitaMin()).compareTo(h.getHoraFin()) <= 0) {
                    slots.add(SlotDisponibleResponse.builder()
                        .medicoId(medicoId)
                        .fecha(fecha.toString())
                        .horaInicio(actual)
                        .horaFin(actual.plusMinutes(h.getDuracionCitaMin()))
                        .disponible(true)
                        .build());
                    actual = actual.plusMinutes(h.getDuracionCitaMin());
                }
            });
        return slots;
    }
}
