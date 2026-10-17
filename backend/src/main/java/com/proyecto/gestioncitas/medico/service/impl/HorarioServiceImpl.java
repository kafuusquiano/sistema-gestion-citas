package com.proyecto.gestioncitas.medico.service.impl;

import com.proyecto.gestioncitas.common.constants.AppConstants;
import com.proyecto.gestioncitas.common.exception.BusinessException;
import com.proyecto.gestioncitas.common.exception.ErrorCode;
import com.proyecto.gestioncitas.common.exception.ResourceNotFoundException;
import com.proyecto.gestioncitas.medico.domain.dto.request.ExcepcionHorarioRequest;
import com.proyecto.gestioncitas.medico.domain.dto.request.HorarioRequest;
import com.proyecto.gestioncitas.medico.domain.dto.response.ExcepcionHorarioResponse;
import com.proyecto.gestioncitas.medico.domain.dto.response.HorarioResponse;
import com.proyecto.gestioncitas.medico.domain.entity.ExcepcionHorario;
import com.proyecto.gestioncitas.medico.domain.entity.Horario;
import com.proyecto.gestioncitas.medico.domain.entity.Medico;
import com.proyecto.gestioncitas.medico.domain.enums.DiaSemana;
import com.proyecto.gestioncitas.medico.event.producers.MedicoEventProducer;
import com.proyecto.gestioncitas.medico.exception.MedicoNoEncontradoException;
import com.proyecto.gestioncitas.medico.mapper.HorarioMapper;
import com.proyecto.gestioncitas.medico.repository.ExcepcionHorarioRepository;
import com.proyecto.gestioncitas.medico.repository.HorarioRepository;
import com.proyecto.gestioncitas.medico.repository.MedicoRepository;
import com.proyecto.gestioncitas.medico.service.HorarioService;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class HorarioServiceImpl implements HorarioService {

    private static final Logger log = LoggerFactory.getLogger(HorarioServiceImpl.class);

    private final MedicoRepository medicoRepository;
    private final HorarioRepository horarioRepository;
    private final ExcepcionHorarioRepository excepcionRepository;
    private final HorarioMapper mapper;
    private final MedicoEventProducer eventos;

    public HorarioServiceImpl(MedicoRepository medicoRepository,
            HorarioRepository horarioRepository,
            ExcepcionHorarioRepository excepcionRepository,
            HorarioMapper mapper,
            MedicoEventProducer eventos) {
        this.medicoRepository = medicoRepository;
        this.horarioRepository = horarioRepository;
        this.excepcionRepository = excepcionRepository;
        this.mapper = mapper;
        this.eventos = eventos;
    }

    @Override
    @Transactional
    @CacheEvict(value = AppConstants.CACHE_HORARIOS, key = "#medicoId")
    public List<HorarioResponse> reemplazarHorarios(Long medicoId, List<HorarioRequest> peticiones) {
        Medico medico = obtenerMedico(medicoId);

        List<Horario> nuevos = new ArrayList<>();
        for (HorarioRequest p : peticiones) {
            // Si no se envia la duracion, la decide la subclase del medico:
            // 20 minutos un general, 30 un especialista. Es el polimorfismo
            // aplicado a una regla operativa.
            short duracion = p.duracionSlotMinutos() != null
                    ? p.duracionSlotMinutos()
                    : (short) medico.duracionConsultaMinutos();

            Horario horario = new Horario(p.diaSemana(), p.horaInicio(), p.horaFin(), duracion);
            validarSinCruces(horario, nuevos);
            nuevos.add(horario);
        }

        medico.reemplazarHorarios(nuevos);

        // Avisa a cita para que invalide su cache de slots. Sin esto seguiria
        // ofreciendo los turnos del horario anterior durante 5 minutos.
        eventos.horarioActualizado(medicoId);

        log.info("Horario del medico {} reemplazado con {} franja(s)", medicoId, nuevos.size());
        return mapper.aResponses(medico.getHorarios());
    }

    @Override
    @Cacheable(value = AppConstants.CACHE_HORARIOS, key = "#medicoId")
    public List<HorarioResponse> obtenerHorarios(Long medicoId) {
        if (!medicoRepository.existsById(medicoId)) {
            throw new MedicoNoEncontradoException(medicoId);
        }
        return mapper.aResponses(
                horarioRepository.findByMedicoIdOrderByDiaSemanaAscHoraInicioAsc(medicoId));
    }

    @Override
    public List<HorarioResponse> obtenerHorariosDeFecha(Long medicoId, LocalDate fecha) {
        // Una excepcion que cubre la fecha deja al medico sin franjas ese dia,
        // aunque su plantilla semanal diga que atiende.
        if (tieneExcepcion(medicoId, fecha)) {
            return List.of();
        }
        return mapper.aResponses(horarioRepository.findByMedicoIdAndDiaSemanaAndActivoTrue(
                medicoId, DiaSemana.desde(fecha)));
    }

    @Override
    @Transactional
    @CacheEvict(value = AppConstants.CACHE_HORARIOS, key = "#medicoId")
    public ExcepcionHorarioResponse registrarExcepcion(Long medicoId,
            ExcepcionHorarioRequest peticion) {
        Medico medico = obtenerMedico(medicoId);

        ExcepcionHorario excepcion = excepcionRepository.save(new ExcepcionHorario(
                medico, peticion.fechaInicio(), peticion.fechaFin(),
                peticion.motivo(), normalizar(peticion.descripcion())));

        eventos.excepcionRegistrada(medicoId);

        log.info("Excepcion {} registrada para el medico {} del {} al {}",
                excepcion.getMotivo(), medicoId, peticion.fechaInicio(), peticion.fechaFin());
        return mapper.aExcepcionResponse(excepcion);
    }

    @Override
    @Transactional
    @CacheEvict(value = AppConstants.CACHE_HORARIOS, allEntries = true)
    public ExcepcionHorarioResponse registrarExcepcionGeneral(ExcepcionHorarioRequest peticion) {
        // Medico nulo: aplica a todo el establecimiento. Por eso se invalida
        // la cache completa y no la de un medico.
        ExcepcionHorario excepcion = excepcionRepository.save(new ExcepcionHorario(
                null, peticion.fechaInicio(), peticion.fechaFin(),
                peticion.motivo(), normalizar(peticion.descripcion())));

        log.info("Excepcion general {} del {} al {}",
                excepcion.getMotivo(), peticion.fechaInicio(), peticion.fechaFin());
        return mapper.aExcepcionResponse(excepcion);
    }

    @Override
    public List<ExcepcionHorarioResponse> listarExcepciones(Long medicoId) {
        return mapper.aExcepcionResponses(
                excepcionRepository.findByMedicoIdOrderByFechaInicioDesc(medicoId));
    }

    @Override
    public List<ExcepcionHorarioResponse> listarExcepcionesGenerales() {
        return mapper.aExcepcionResponses(
                excepcionRepository.findByMedicoIsNullOrderByFechaInicioDesc());
    }

    @Override
    @Transactional
    @CacheEvict(value = AppConstants.CACHE_HORARIOS, allEntries = true)
    public void eliminarExcepcion(Long excepcionId) {
        ExcepcionHorario excepcion = excepcionRepository.findById(excepcionId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        ErrorCode.RECURSO_NO_ENCONTRADO, "Excepcion de horario", excepcionId));

        Long medicoId = excepcion.getMedico() == null ? null : excepcion.getMedico().getId();
        excepcionRepository.delete(excepcion);

        if (medicoId != null) {
            eventos.excepcionRegistrada(medicoId);
        }
        log.info("Excepcion {} eliminada", excepcionId);
    }

    @Override
    public boolean tieneExcepcion(Long medicoId, LocalDate fecha) {
        return !excepcionRepository.listarQueCubren(medicoId, fecha).isEmpty();
    }

    // ---------- Apoyo ----------

    private Medico obtenerMedico(Long medicoId) {
        return medicoRepository.findById(medicoId)
                .orElseThrow(() -> new MedicoNoEncontradoException(medicoId));
    }

    /**
     * Dos franjas del mismo dia no pueden solaparse.
     *
     * La base no puede expresar esta regla con un CHECK, porque compara filas
     * distintas de la misma tabla. Sin la validacion, un medico podria quedar
     * con dos franjas cruzadas y el mismo turno ofrecido dos veces.
     */
    private void validarSinCruces(Horario candidato, List<Horario> yaAgregados) {
        for (Horario existente : yaAgregados) {
            if (candidato.seCruzaCon(existente)) {
                throw new BusinessException(ErrorCode.HORARIO_SUPERPUESTO,
                        "La franja del %s de %s a %s se cruza con la de %s a %s".formatted(
                                candidato.getDiaSemana(), candidato.getHoraInicio(),
                                candidato.getHoraFin(), existente.getHoraInicio(),
                                existente.getHoraFin()));
            }
        }
    }

    private String normalizar(String valor) {
        return (valor == null || valor.isBlank()) ? null : valor.trim();
    }
}
