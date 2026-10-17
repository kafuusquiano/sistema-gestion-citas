package com.proyecto.gestioncitas.medico.service.impl;

import com.proyecto.gestioncitas.auth.api.AuthFacade;
import com.proyecto.gestioncitas.auth.domain.enums.Rol;
import com.proyecto.gestioncitas.common.dto.PageResponse;
import com.proyecto.gestioncitas.common.exception.BusinessException;
import com.proyecto.gestioncitas.common.exception.ErrorCode;
import com.proyecto.gestioncitas.medico.domain.dto.request.ActualizarMedicoRequest;
import com.proyecto.gestioncitas.medico.domain.dto.request.CrearMedicoRequest;
import com.proyecto.gestioncitas.medico.domain.dto.response.MedicoResponse;
import com.proyecto.gestioncitas.medico.domain.dto.response.MedicoResumenResponse;
import com.proyecto.gestioncitas.medico.domain.entity.Especialidad;
import com.proyecto.gestioncitas.medico.domain.entity.Medico;
import com.proyecto.gestioncitas.medico.domain.entity.MedicoEspecialista;
import com.proyecto.gestioncitas.medico.domain.entity.MedicoGeneral;
import com.proyecto.gestioncitas.medico.domain.enums.EstadoMedico;
import com.proyecto.gestioncitas.medico.domain.enums.TipoMedico;
import com.proyecto.gestioncitas.medico.event.producers.MedicoEventProducer;
import com.proyecto.gestioncitas.medico.exception.MedicoNoEncontradoException;
import com.proyecto.gestioncitas.medico.mapper.MedicoMapper;
import com.proyecto.gestioncitas.medico.repository.MedicoRepository;
import com.proyecto.gestioncitas.medico.service.EspecialidadService;
import com.proyecto.gestioncitas.medico.service.MedicoService;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class MedicoServiceImpl implements MedicoService {

    private static final Logger log = LoggerFactory.getLogger(MedicoServiceImpl.class);

    private final MedicoRepository repositorio;
    private final EspecialidadService especialidadService;
    private final AuthFacade authFacade;
    private final MedicoMapper mapper;
    private final MedicoEventProducer eventos;

    public MedicoServiceImpl(MedicoRepository repositorio,
            EspecialidadService especialidadService,
            AuthFacade authFacade,
            MedicoMapper mapper,
            MedicoEventProducer eventos) {
        this.repositorio = repositorio;
        this.especialidadService = especialidadService;
        this.authFacade = authFacade;
        this.mapper = mapper;
        this.eventos = eventos;
    }

    @Override
    @Transactional
    public MedicoResponse crear(CrearMedicoRequest peticion) {
        if (repositorio.existsByCmp(peticion.cmp())) {
            throw new BusinessException(ErrorCode.CONFLICTO_DE_ESTADO,
                    "Ya existe un medico con CMP %s".formatted(peticion.cmp()));
        }

        Especialidad especialidad = especialidadService.obtenerEntidad(peticion.especialidadId());
        if (!especialidad.isActivo()) {
            throw new BusinessException(ErrorCode.CONFLICTO_DE_ESTADO,
                    "La especialidad %s esta desactivada".formatted(especialidad.getNombre()));
        }

        // La cuenta se pide por la Facade de auth. Instanciar un Usuario aqui
        // seria importar una entidad de otro modulo y romper la regla M1.
        Long usuarioId = authFacade.crearCuenta(new AuthFacade.NuevaCuenta(
                peticion.email(), peticion.dni(), peticion.password(), Rol.MEDICO));

        Medico medico = construirSegunTipo(peticion, especialidad, usuarioId);
        Medico guardado = repositorio.save(medico);

        log.info("Medico {} dado de alta como {} con CMP {} y cuenta {}",
                guardado.getId(), guardado.tipo(), guardado.getCmp(), usuarioId);

        return mapper.aResponse(guardado);
    }

    /**
     * Elige la subclase segun el tipo. Es el unico punto del modulo donde se
     * decide que clase instanciar; de ahi en adelante todo el codigo trabaja
     * contra la clase base Medico y el polimorfismo hace el resto.
     */
    private Medico construirSegunTipo(CrearMedicoRequest p, Especialidad especialidad,
            Long usuarioId) {
        return p.tipo() == TipoMedico.ESPECIALISTA
                ? new MedicoEspecialista(usuarioId, p.cmp(), p.nombres().trim(),
                        p.apellidoPaterno().trim(), normalizar(p.apellidoMaterno()),
                        especialidad, p.rne(), p.aniosExperiencia())
                : new MedicoGeneral(usuarioId, p.cmp(), p.nombres().trim(),
                        p.apellidoPaterno().trim(), normalizar(p.apellidoMaterno()),
                        especialidad, normalizar(p.areaRotacion()));
    }

    @Override
    public PageResponse<MedicoResumenResponse> buscar(Long especialidadId, EstadoMedico estado,
            String texto, Pageable pageable) {
        String filtro = (texto == null || texto.isBlank()) ? null : texto.trim();
        return PageResponse.de(
                repositorio.buscar(especialidadId, estado, filtro, pageable),
                mapper::aResumen);
    }

    @Override
    public List<MedicoResumenResponse> listarActivosPorEspecialidad(Long especialidadId) {
        return repositorio.listarActivosPorEspecialidad(especialidadId).stream()
                .map(mapper::aResumen)
                .toList();
    }

    @Override
    public MedicoResponse obtener(Long id) {
        // Con EntityGraph para traer especialidad y horarios en una consulta.
        return repositorio.findWithHorariosById(id)
                .map(mapper::aResponse)
                .orElseThrow(() -> new MedicoNoEncontradoException(id));
    }

    @Override
    public MedicoResponse obtenerPorUsuario(Long usuarioId) {
        return repositorio.findByUsuarioId(usuarioId)
                .map(mapper::aResponse)
                .orElseThrow(() -> MedicoNoEncontradoException.porUsuario(usuarioId));
    }

    @Override
    @Transactional
    public MedicoResponse actualizar(Long id, ActualizarMedicoRequest peticion) {
        Medico medico = obtenerEntidad(id);

        medico.actualizarDatos(
                peticion.nombres().trim(),
                peticion.apellidoPaterno().trim(),
                normalizar(peticion.apellidoMaterno()),
                normalizar(peticion.telefono()));

        if (!medico.getEspecialidad().getId().equals(peticion.especialidadId())) {
            medico.cambiarEspecialidad(
                    especialidadService.obtenerEntidad(peticion.especialidadId()));
        }

        // Los campos propios de cada subclase se aplican solo a quien
        // corresponde; en la otra el dato se ignora en silencio porque el DTO
        // es comun a las dos.
        if (medico instanceof MedicoEspecialista esp && peticion.aniosExperiencia() != null) {
            esp.actualizarExperiencia(peticion.aniosExperiencia());
        }
        if (medico instanceof MedicoGeneral gen && peticion.areaRotacion() != null) {
            gen.cambiarAreaRotacion(normalizar(peticion.areaRotacion()));
        }

        log.info("Medico {} actualizado", id);
        return mapper.aResponse(medico);
    }

    @Override
    @Transactional
    public MedicoResponse cambiarEstado(Long id, EstadoMedico estado) {
        Medico medico = obtenerEntidad(id);
        EstadoMedico anterior = medico.getEstado();
        medico.cambiarEstado(estado);

        // Si deja de admitir citas, su disponibilidad cambio: hay que botar la
        // cache de slots o seguiria ofreciendo cupos de un medico de licencia.
        if (anterior.admiteCitas() != estado.admiteCitas()) {
            eventos.estadoCambiado(id);
        }

        log.info("Medico {} paso de {} a {}", id, anterior, estado);
        return mapper.aResponse(medico);
    }

    @Override
    public Medico obtenerEntidad(Long id) {
        return repositorio.findById(id)
                .orElseThrow(() -> new MedicoNoEncontradoException(id));
    }

    private String normalizar(String valor) {
        return (valor == null || valor.isBlank()) ? null : valor.trim();
    }
}
