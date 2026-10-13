package com.proyecto.gestioncitas.paciente.service.impl;

import com.proyecto.gestioncitas.common.dto.PageResponse;
import com.proyecto.gestioncitas.common.exception.BusinessException;
import com.proyecto.gestioncitas.common.exception.ErrorCode;
import com.proyecto.gestioncitas.paciente.domain.dto.request.ActualizarPacienteRequest;
import com.proyecto.gestioncitas.paciente.domain.dto.request.CrearPacienteRequest;
import com.proyecto.gestioncitas.paciente.domain.dto.request.PacienteFiltroRequest;
import com.proyecto.gestioncitas.paciente.domain.dto.response.PacienteResponse;
import com.proyecto.gestioncitas.paciente.domain.dto.response.PacienteResumenResponse;
import com.proyecto.gestioncitas.paciente.domain.entity.ContactoEmergencia;
import com.proyecto.gestioncitas.paciente.domain.entity.Paciente;
import com.proyecto.gestioncitas.paciente.domain.enums.GrupoSanguineo;
import com.proyecto.gestioncitas.paciente.domain.enums.Sexo;
import com.proyecto.gestioncitas.paciente.domain.enums.TipoDocumento;
import com.proyecto.gestioncitas.paciente.exception.PacienteNoEncontradoException;
import com.proyecto.gestioncitas.paciente.mapper.PacienteMapper;
import com.proyecto.gestioncitas.paciente.repository.PacienteRepository;
import com.proyecto.gestioncitas.paciente.repository.spec.PacienteSpecification;
import com.proyecto.gestioncitas.paciente.service.PacienteService;
import java.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class PacienteServiceImpl implements PacienteService {

    private static final Logger log = LoggerFactory.getLogger(PacienteServiceImpl.class);

    /** Formato del numero de historia clinica: HC-000001. */
    private static final String FORMATO_HISTORIA = "HC-%06d";

    private final PacienteRepository repositorio;
    private final PacienteMapper mapper;

    public PacienteServiceImpl(PacienteRepository repositorio, PacienteMapper mapper) {
        this.repositorio = repositorio;
        this.mapper = mapper;
    }

    @Override
    @Transactional
    public PacienteResponse crear(CrearPacienteRequest peticion) {
        validarDocumentoDisponible(peticion.tipoDocumento(), peticion.numeroDocumento());
        validarLongitudDocumento(peticion.tipoDocumento(), peticion.numeroDocumento());

        Paciente paciente = new Paciente(
                generarNumeroHistoria(),
                peticion.tipoDocumento(),
                peticion.numeroDocumento().trim(),
                peticion.nombres().trim(),
                peticion.apellidoPaterno().trim(),
                normalizar(peticion.apellidoMaterno()),
                peticion.fechaNacimiento(),
                peticion.sexo());

        paciente.actualizarContacto(
                normalizar(peticion.telefono()),
                normalizar(peticion.email()),
                normalizar(peticion.direccion()),
                normalizar(peticion.distrito()));

        paciente.actualizarDatosClinicos(
                GrupoSanguineo.desdeEtiqueta(peticion.grupoSanguineo()),
                peticion.seguro());

        if (peticion.contactos() != null) {
            peticion.contactos().forEach(c -> paciente.agregarContacto(
                    new ContactoEmergencia(c.nombres().trim(), c.parentesco().trim(),
                            c.telefono().trim(), c.esPrincipal())));
        }

        Paciente guardado = repositorio.save(paciente);
        log.info("Paciente {} registrado con historia {}",
                guardado.getId(), guardado.getNumeroHistoria());

        return mapper.aResponse(guardado);
    }

    @Override
    @Transactional
    public void crearDesdeRegistro(Long usuarioId, String numeroDocumento, String nombres,
            String apellidoPaterno, String apellidoMaterno, LocalDate fechaNacimiento,
            String sexo, String telefono, String email) {

        // El registro publico siempre usa DNI: RegisterRequest lo valida con
        // el patron de 8 digitos.
        if (repositorio.existsByTipoDocumentoAndNumeroDocumento(TipoDocumento.DNI, numeroDocumento)) {
            // La cuenta ya se creo, asi que el documento duplicado aqui
            // significa que el paciente ya tenia ficha de una atencion por
            // ventanilla. Se vincula la ficha existente en vez de fallar.
            Paciente existente = repositorio
                    .findByTipoDocumentoAndNumeroDocumento(TipoDocumento.DNI, numeroDocumento)
                    .orElseThrow(() -> PacienteNoEncontradoException.porDocumento(numeroDocumento));

            if (!existente.tieneCuenta()) {
                existente.vincularUsuario(usuarioId);
                log.info("Ficha {} existente vinculada a la cuenta {}",
                        existente.getId(), usuarioId);
            }
            return;
        }

        Paciente paciente = new Paciente(
                generarNumeroHistoria(),
                TipoDocumento.DNI,
                numeroDocumento,
                nombres.trim(),
                apellidoPaterno.trim(),
                normalizar(apellidoMaterno),
                fechaNacimiento,
                Sexo.valueOf(sexo));

        paciente.vincularUsuario(usuarioId);
        paciente.actualizarContacto(normalizar(telefono), normalizar(email), null, null);

        Paciente guardado = repositorio.save(paciente);
        log.info("Ficha {} creada desde el registro de la cuenta {} con historia {}",
                guardado.getId(), usuarioId, guardado.getNumeroHistoria());
    }

    @Override
    public PageResponse<PacienteResumenResponse> buscar(PacienteFiltroRequest filtro,
            Pageable pageable) {
        PacienteFiltroRequest efectivo = filtro == null ? PacienteFiltroRequest.vacio() : filtro;
        return PageResponse.de(
                repositorio.findAll(PacienteSpecification.con(efectivo), pageable),
                mapper::aResumen);
    }

    @Override
    public PacienteResponse obtener(Long id) {
        return mapper.aResponse(obtenerEntidad(id));
    }

    @Override
    public PacienteResponse obtenerPorUsuario(Long usuarioId) {
        return repositorio.findByUsuarioId(usuarioId)
                .map(mapper::aResponse)
                .orElseThrow(() -> PacienteNoEncontradoException.porUsuario(usuarioId));
    }

    @Override
    @Transactional
    public PacienteResponse actualizar(Long id, ActualizarPacienteRequest peticion) {
        Paciente paciente = obtenerEntidad(id);

        paciente.actualizarIdentidad(
                peticion.nombres().trim(),
                peticion.apellidoPaterno().trim(),
                normalizar(peticion.apellidoMaterno()),
                peticion.fechaNacimiento(),
                peticion.sexo());

        paciente.actualizarContacto(
                normalizar(peticion.telefono()),
                normalizar(peticion.email()),
                normalizar(peticion.direccion()),
                normalizar(peticion.distrito()));

        paciente.actualizarDatosClinicos(
                GrupoSanguineo.desdeEtiqueta(peticion.grupoSanguineo()),
                peticion.seguro());

        log.info("Ficha {} actualizada", id);
        return mapper.aResponse(paciente);
    }

    @Override
    @Transactional
    public void darDeBaja(Long id) {
        Paciente paciente = obtenerEntidad(id);
        paciente.dardeBaja();
        log.info("Ficha {} dada de baja", id);
    }

    @Override
    @Transactional
    public PacienteResponse reactivar(Long id) {
        Paciente paciente = obtenerEntidad(id);
        paciente.reactivar();
        log.info("Ficha {} reactivada", id);
        return mapper.aResponse(paciente);
    }

    // ---------- Apoyo ----------

    private Paciente obtenerEntidad(Long id) {
        return repositorio.findById(id)
                .orElseThrow(() -> new PacienteNoEncontradoException(id));
    }

    private void validarDocumentoDisponible(TipoDocumento tipo, String numero) {
        if (repositorio.existsByTipoDocumentoAndNumeroDocumento(tipo, numero.trim())) {
            throw new BusinessException(ErrorCode.DNI_YA_REGISTRADO,
                    "Ya existe un paciente con el documento %s".formatted(numero));
        }
    }

    private void validarLongitudDocumento(TipoDocumento tipo, String numero) {
        if (!tipo.longitudValida(numero)) {
            throw new BusinessException(ErrorCode.DATOS_INVALIDOS,
                    "Un %s debe tener entre %d y %d digitos".formatted(
                            tipo.name(), tipo.getLongitudMinima(), tipo.getLongitudMaxima()));
        }
    }

    /**
     * Toma el correlativo de la secuencia de V7. No se usa MAX()+1 porque con
     * dos altas simultaneas en admision ambas obtendrian el mismo numero y una
     * fallaria contra el indice unico.
     */
    private String generarNumeroHistoria() {
        return FORMATO_HISTORIA.formatted(repositorio.siguienteNumeroHistoria());
    }

    private String normalizar(String valor) {
        return (valor == null || valor.isBlank()) ? null : valor.trim();
    }
}
