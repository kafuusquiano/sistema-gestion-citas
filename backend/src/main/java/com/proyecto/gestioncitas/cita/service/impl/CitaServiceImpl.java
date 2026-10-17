package com.proyecto.gestioncitas.cita.service.impl;

import com.proyecto.gestioncitas.cita.domain.dto.request.CancelarRequest;
import com.proyecto.gestioncitas.cita.domain.dto.request.CitaRequest;
import com.proyecto.gestioncitas.cita.domain.dto.request.ReprogramarRequest;
import com.proyecto.gestioncitas.cita.domain.dto.response.CitaResponse;
import com.proyecto.gestioncitas.cita.domain.entity.Cita;
import com.proyecto.gestioncitas.cita.domain.enums.EstadoCita;
import com.proyecto.gestioncitas.cita.event.producers.CitaEventProducer;
import com.proyecto.gestioncitas.cita.exception.SlotNoDisponibleException;
import com.proyecto.gestioncitas.cita.mapper.CitaMapper;
import com.proyecto.gestioncitas.cita.repository.CitaRepository;
import com.proyecto.gestioncitas.cita.service.CitaService;
import com.proyecto.gestioncitas.cita.service.DisponibilidadService;
import com.proyecto.gestioncitas.common.constants.AppConstants;
import com.proyecto.gestioncitas.common.dto.PageResponse;
import com.proyecto.gestioncitas.common.exception.BusinessException;
import com.proyecto.gestioncitas.common.exception.ErrorCode;
import com.proyecto.gestioncitas.common.exception.ResourceNotFoundException;
import com.proyecto.gestioncitas.common.util.DateUtils;
import com.proyecto.gestioncitas.medico.api.MedicoFacade;
import com.proyecto.gestioncitas.paciente.api.PacienteFacade;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Programacion y seguimiento de citas.
 *
 * Toda operacion que mueve un cupo descarta la cache de disponibilidad, o
 * seguiria ofreciendo turnos que ya no existen durante cinco minutos.
 */
@Service
@Transactional(readOnly = true)
public class CitaServiceImpl implements CitaService {

    private static final Logger log = LoggerFactory.getLogger(CitaServiceImpl.class);

    private final CitaRepository repositorio;
    private final DisponibilidadService disponibilidad;
    private final CitaEstadoValidator validador;
    private final PacienteFacade pacienteFacade;
    private final MedicoFacade medicoFacade;
    private final CitaMapper mapper;
    private final CitaEventProducer eventos;

    public CitaServiceImpl(CitaRepository repositorio,
            DisponibilidadService disponibilidad,
            CitaEstadoValidator validador,
            PacienteFacade pacienteFacade,
            MedicoFacade medicoFacade,
            CitaMapper mapper,
            CitaEventProducer eventos) {
        this.repositorio = repositorio;
        this.disponibilidad = disponibilidad;
        this.validador = validador;
        this.pacienteFacade = pacienteFacade;
        this.medicoFacade = medicoFacade;
        this.mapper = mapper;
        this.eventos = eventos;
    }

    @Override
    @Transactional
    @CacheEvict(value = AppConstants.CACHE_SLOTS, allEntries = true)
    public CitaResponse agendar(CitaRequest peticion, Long usuarioId) {
        Long pacienteId = resolverPaciente(peticion.pacienteId(), usuarioId);

        if (!pacienteFacade.puedeAgendar(pacienteId)) {
            throw new BusinessException(ErrorCode.CONFLICTO_DE_ESTADO,
                    "El paciente esta inactivo y no puede agendar citas");
        }
        if (!medicoFacade.admiteCitas(peticion.medicoId())) {
            throw new BusinessException(ErrorCode.CONFLICTO_DE_ESTADO,
                    "El medico no esta disponible para recibir citas");
        }

        Instant fechaHora = DateUtils.aInstante(peticion.fechaHora());

        // Se distinguen los dos fallos porque el usuario los vive distinto:
        // una hora en la que el medico no atiende no es lo mismo que una hora
        // valida que otro paciente ya tomo. Antes ambos casos devolvian el
        // mismo mensaje y el segundo mentia sobre el primero.
        if (!disponibilidad.esHorarioDeAtencion(peticion.medicoId(), fechaHora)) {
            throw SlotNoDisponibleException.fueraDeHorario(peticion.fechaHora());
        }
        // Atrapa el caso habitual y da un mensaje util, pero no basta por si
        // sola: dos operadores pueden pasarla a la vez.
        if (!disponibilidad.esSlotValido(peticion.medicoId(), fechaHora)) {
            throw new SlotNoDisponibleException(peticion.fechaHora());
        }

        short duracion = (short) disponibilidad.duracionSlotDelMedico(
                peticion.medicoId(), peticion.fechaHora().toLocalDate());

        Cita cita = new Cita(pacienteId, peticion.medicoId(), fechaHora, duracion,
                peticion.tipoOConsulta(), peticion.motivo());

        Cita guardada = persistirControlandoDobleReserva(cita, peticion.fechaHora());

        eventos.citaCreada(guardada);
        log.info("Cita {} agendada: paciente {}, medico {}, {}",
                guardada.getId(), pacienteId, peticion.medicoId(), peticion.fechaHora());

        return mapper.aResponse(guardada);
    }

    @Override
    public CitaResponse obtener(Long id) {
        return mapper.aResponse(obtenerConHistorial(id));
    }

    @Override
    public PageResponse<CitaResponse> buscar(Long pacienteId, Long medicoId, EstadoCita estado,
            LocalDate desde, LocalDate hasta, Pageable pageable) {

        Instant inicio = desde == null ? null : DateUtils.aInstante(desde.atStartOfDay());
        Instant fin = hasta == null ? null : DateUtils.aInstante(hasta.plusDays(1).atStartOfDay());

        return PageResponse.de(
                repositorio.buscar(pacienteId, medicoId, estado, inicio, fin, pageable),
                mapper::aResponse);
    }

    @Override
    public PageResponse<CitaResponse> misCitas(Long usuarioId, Pageable pageable) {
        Long pacienteId = pacienteFacade.obtenerIdPorUsuario(usuarioId);
        if (pacienteId == null) {
            throw new BusinessException(ErrorCode.PACIENTE_NO_ENCONTRADO,
                    "Su cuenta no tiene una ficha de paciente asociada");
        }
        return PageResponse.de(
                repositorio.findByPacienteIdOrderByFechaHoraDesc(pacienteId, pageable),
                mapper::aResponse);
    }

    @Override
    public List<CitaResponse> agendaDelMedico(Long medicoId, LocalDate fecha) {
        Instant desde = DateUtils.aInstante(fecha.atStartOfDay());
        Instant hasta = DateUtils.aInstante(fecha.plusDays(1).atStartOfDay());
        return mapper.aResponses(repositorio.listarAgendaDelMedico(medicoId, desde, hasta));
    }

    @Override
    @Transactional
    public CitaResponse confirmar(Long id, Long usuarioId) {
        Cita cita = obtenerConHistorial(id);
        validador.validarTransicion(cita, EstadoCita.CONFIRMADA);
        cita.confirmar(usuarioId);

        log.info("Cita {} confirmada", id);
        return mapper.aResponse(cita);
    }

    @Override
    @Transactional
    @CacheEvict(value = AppConstants.CACHE_SLOTS, allEntries = true)
    public CitaResponse cancelar(Long id, CancelarRequest peticion, Long usuarioId,
            boolean esPersonal) {
        Cita cita = obtenerConHistorial(id);
        validador.validarCancelacion(cita, esPersonal);

        cita.cancelar(peticion.motivo(), usuarioId);
        eventos.citaCancelada(cita);

        // La cita cancelada sale del indice unico parcial, asi que su turno
        // vuelve a ofrecerse.
        log.info("Cita {} cancelada: {}", id, peticion.motivo());
        return mapper.aResponse(cita);
    }

    @Override
    @Transactional
    @CacheEvict(value = AppConstants.CACHE_SLOTS, allEntries = true)
    public CitaResponse reprogramar(Long id, ReprogramarRequest peticion, Long usuarioId) {
        Cita cita = obtenerConHistorial(id);
        validador.validarReprogramacion(cita);

        Instant nueva = DateUtils.aInstante(peticion.nuevaFechaHora());
        if (nueva.equals(cita.getFechaHora())) {
            throw new BusinessException(ErrorCode.DATOS_INVALIDOS,
                    "La nueva fecha es la misma que la actual");
        }
        if (!disponibilidad.esHorarioDeAtencion(cita.getMedicoId(), nueva)) {
            throw SlotNoDisponibleException.fueraDeHorario(peticion.nuevaFechaHora());
        }
        if (!disponibilidad.esSlotValido(cita.getMedicoId(), nueva)) {
            throw new SlotNoDisponibleException(peticion.nuevaFechaHora());
        }

        LocalDateTime anterior = cita.fechaHoraLocal();
        cita.reprogramar(nueva, peticion.motivo(), usuarioId);

        // Se fuerza la escritura ahora para que, si el turno nuevo fue tomado
        // en paralelo, el indice unico falle aqui y no al cerrar la transaccion.
        persistirControlandoDobleReserva(cita, peticion.nuevaFechaHora());

        eventos.citaReprogramada(cita,
                "Movida del %s al %s".formatted(anterior, peticion.nuevaFechaHora()));

        log.info("Cita {} reprogramada del {} al {}", id, anterior, peticion.nuevaFechaHora());
        return mapper.aResponse(cita);
    }

    @Override
    @Transactional
    public CitaResponse completar(Long id, String observaciones, Long usuarioId) {
        Cita cita = obtenerConHistorial(id);
        validador.validarCompletar(cita);

        cita.completar(observaciones, usuarioId);
        eventos.citaCompletada(cita);

        log.info("Cita {} completada", id);
        return mapper.aResponse(cita);
    }

    @Override
    @Transactional
    @CacheEvict(value = AppConstants.CACHE_SLOTS, allEntries = true)
    public CitaResponse marcarNoAsistio(Long id, Long usuarioId) {
        Cita cita = obtenerConHistorial(id);
        validador.validarTransicion(cita, EstadoCita.NO_ASISTIO);

        cita.marcarNoAsistio(usuarioId);
        log.info("Cita {} marcada como inasistencia", id);
        return mapper.aResponse(cita);
    }

    // ---------- Apoyo ----------

    private Cita obtenerConHistorial(Long id) {
        return repositorio.findWithHistorialById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        ErrorCode.CITA_NO_ENCONTRADA, "Cita", id));
    }

    /**
     * Si el pacienteId no viene en la peticion, es el propio paciente quien
     * agenda y se toma su ficha del token. Si viene, lo hace personal de
     * admision en nombre de otro.
     */
    private Long resolverPaciente(Long pacienteIdPedido, Long usuarioId) {
        if (pacienteIdPedido != null) {
            if (!pacienteFacade.existe(pacienteIdPedido)) {
                throw new ResourceNotFoundException(
                        ErrorCode.PACIENTE_NO_ENCONTRADO, "Paciente", pacienteIdPedido);
            }
            return pacienteIdPedido;
        }

        Long propio = pacienteFacade.obtenerIdPorUsuario(usuarioId);
        if (propio == null) {
            throw new BusinessException(ErrorCode.PACIENTE_NO_ENCONTRADO,
                    "Indique el paciente: su cuenta no tiene una ficha asociada");
        }
        return propio;
    }

    /**
     * Guarda traduciendo el rechazo del indice unico a un error de negocio.
     *
     * Es la red de seguridad contra la doble reserva simultanea: si dos
     * operadores reservan el mismo cupo a la vez, ambos pasan la verificacion
     * previa porque ninguna cita se ha guardado todavia, y es la base de datos
     * la que rechaza la segunda insercion.
     */
    private Cita persistirControlandoDobleReserva(Cita cita, LocalDateTime fechaHora) {
        try {
            return repositorio.saveAndFlush(cita);
        } catch (DataIntegrityViolationException e) {
            log.warn("Choque contra uk_citas_medico_slot al reservar {}", fechaHora);
            throw new SlotNoDisponibleException(fechaHora);
        }
    }
}
