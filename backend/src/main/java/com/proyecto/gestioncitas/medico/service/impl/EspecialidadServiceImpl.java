package com.proyecto.gestioncitas.medico.service.impl;

import com.proyecto.gestioncitas.common.constants.AppConstants;
import com.proyecto.gestioncitas.common.exception.BusinessException;
import com.proyecto.gestioncitas.common.exception.ErrorCode;
import com.proyecto.gestioncitas.common.exception.ResourceNotFoundException;
import com.proyecto.gestioncitas.medico.domain.dto.request.EspecialidadRequest;
import com.proyecto.gestioncitas.medico.domain.dto.response.EspecialidadResponse;
import com.proyecto.gestioncitas.medico.domain.entity.Especialidad;
import com.proyecto.gestioncitas.medico.domain.enums.EstadoMedico;
import com.proyecto.gestioncitas.medico.mapper.MedicoMapper;
import com.proyecto.gestioncitas.medico.repository.EspecialidadRepository;
import com.proyecto.gestioncitas.medico.repository.MedicoRepository;
import com.proyecto.gestioncitas.medico.service.EspecialidadService;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class EspecialidadServiceImpl implements EspecialidadService {


    private static final Logger log = LoggerFactory.getLogger(EspecialidadServiceImpl.class);

    private final EspecialidadRepository repositorio;
    private final MedicoRepository medicoRepository;
    private final MedicoMapper mapper;

    public EspecialidadServiceImpl(EspecialidadRepository repositorio,
            MedicoRepository medicoRepository,
            MedicoMapper mapper) {
        this.repositorio = repositorio;
        this.medicoRepository = medicoRepository;
        this.mapper = mapper;
    }

    @Override
    @Transactional
    @CacheEvict(value = AppConstants.CACHE_ESPECIALIDADES, allEntries = true)
    public EspecialidadResponse crear(EspecialidadRequest peticion) {
        String nombre = peticion.nombre().trim();
        if (repositorio.existsByNombreIgnoreCase(nombre)) {
            throw new BusinessException(ErrorCode.CONFLICTO_DE_ESTADO,
                    "Ya existe la especialidad %s".formatted(nombre));
        }

        Especialidad guardada = repositorio.save(
                new Especialidad(nombre, normalizar(peticion.descripcion())));
        log.info("Especialidad {} creada con id {}", nombre, guardada.getId());

        return mapper.aEspecialidadResponse(guardada);
    }

    @Override
    @Transactional
    @CacheEvict(value = AppConstants.CACHE_ESPECIALIDADES, allEntries = true)
    public EspecialidadResponse actualizar(Long id, EspecialidadRequest peticion) {
        Especialidad especialidad = obtenerEntidad(id);
        String nombre = peticion.nombre().trim();

        // Permite cambiar la descripcion sin tocar el nombre.
        repositorio.findByNombreIgnoreCase(nombre)
                .filter(otra -> !otra.getId().equals(id))
                .ifPresent(otra -> {
                    throw new BusinessException(ErrorCode.CONFLICTO_DE_ESTADO,
                            "Ya existe la especialidad %s".formatted(nombre));
                });

        especialidad.actualizar(nombre, normalizar(peticion.descripcion()));
        return mapper.aEspecialidadResponse(especialidad);
    }

    @Override
    @Cacheable(AppConstants.CACHE_ESPECIALIDADES)
    public List<EspecialidadResponse> listarActivas() {
        return mapper.aEspecialidadResponses(repositorio.findByActivoTrueOrderByNombreAsc());
    }

    @Override
    public List<EspecialidadResponse> listarTodas() {
        return mapper.aEspecialidadResponses(repositorio.findAllByOrderByNombreAsc());
    }

    @Override
    public EspecialidadResponse obtener(Long id) {
        return mapper.aEspecialidadResponse(obtenerEntidad(id));
    }

    @Override
    @Transactional
    @CacheEvict(value = AppConstants.CACHE_ESPECIALIDADES, allEntries = true)
    public void desactivar(Long id) {
        Especialidad especialidad = obtenerEntidad(id);

        // Desactivar una especialidad con medicos activos dejaria a esos
        // medicos invisibles en el selector y sin forma de recibir citas.
        long activos = medicoRepository.countByEspecialidadIdAndEstado(id, EstadoMedico.ACTIVO);
        if (activos > 0) {
            throw new BusinessException(ErrorCode.CONFLICTO_DE_ESTADO,
                    "No se puede desactivar: hay %d medico(s) activo(s) en esta especialidad"
                            .formatted(activos));
        }

        especialidad.desactivar();
        log.info("Especialidad {} desactivada", id);
    }

    @Override
    @Transactional
    @CacheEvict(value = AppConstants.CACHE_ESPECIALIDADES, allEntries = true)
    public EspecialidadResponse activar(Long id) {
        Especialidad especialidad = obtenerEntidad(id);
        especialidad.activar();
        return mapper.aEspecialidadResponse(especialidad);
    }

    @Override
    public Especialidad obtenerEntidad(Long id) {
        return repositorio.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        ErrorCode.ESPECIALIDAD_NO_ENCONTRADA, "Especialidad", id));
    }

    private String normalizar(String valor) {
        return (valor == null || valor.isBlank()) ? null : valor.trim();
    }
}
