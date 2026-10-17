package com.proyecto.gestioncitas.notificacion.service.impl;

import com.proyecto.gestioncitas.auth.api.AuthFacade;
import com.proyecto.gestioncitas.common.dto.PageResponse;
import com.proyecto.gestioncitas.common.exception.ErrorCode;
import com.proyecto.gestioncitas.common.exception.ResourceNotFoundException;
import com.proyecto.gestioncitas.notificacion.domain.dto.response.NotificacionResponse;
import com.proyecto.gestioncitas.notificacion.domain.entity.Notificacion;
import com.proyecto.gestioncitas.notificacion.domain.enums.CanalNotificacion;
import com.proyecto.gestioncitas.notificacion.domain.enums.EstadoNotificacion;
import com.proyecto.gestioncitas.notificacion.domain.enums.TipoNotificacion;
import com.proyecto.gestioncitas.notificacion.mapper.NotificacionMapper;
import com.proyecto.gestioncitas.notificacion.repository.NotificacionRepository;
import com.proyecto.gestioncitas.notificacion.service.NotificacionService;
import com.proyecto.gestioncitas.notificacion.service.sender.NotificacionSender;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Orquesta la creacion y entrega de avisos.
 *
 * Aqui se ve el polimorfismo en uso: el constructor recibe TODAS las
 * implementaciones de NotificacionSender que existan y las indexa por el canal
 * que cada una declara. Al entregar, se busca la que corresponde y se llama a
 * enviar(). No hay un solo if ni switch sobre el canal.
 *
 * Agregar un canal nuevo es agregar una clase. Esta no cambia.
 */
@Service
@Transactional(readOnly = true)
public class NotificacionServiceImpl implements NotificacionService {

    private static final Logger log = LoggerFactory.getLogger(NotificacionServiceImpl.class);

    /** Tras cinco fallos se deja de insistir: la direccion suele ser invalida. */
    private static final int MAXIMO_INTENTOS = 5;

    private static final int LOTE_REINTENTO = 50;

    private final NotificacionRepository repositorio;
    private final NotificacionMapper mapper;
    private final TemplateRenderer renderer;
    private final AuthFacade authFacade;

    /** Indexados por canal en el arranque, no en cada envio. */
    private final Map<CanalNotificacion, NotificacionSender> enviadores =
            new EnumMap<>(CanalNotificacion.class);

    public NotificacionServiceImpl(NotificacionRepository repositorio,
            NotificacionMapper mapper,
            TemplateRenderer renderer,
            AuthFacade authFacade,
            List<NotificacionSender> todosLosEnviadores) {
        this.repositorio = repositorio;
        this.mapper = mapper;
        this.renderer = renderer;
        this.authFacade = authFacade;

        // Spring inyecta la lista con cada implementacion de la interfaz. Esta
        // clase no las nombra: descubre las que haya.
        todosLosEnviadores.forEach(e -> enviadores.put(e.canal(), e));
        log.info("Canales de notificacion disponibles: {}", enviadores.keySet());
    }

    @Override
    @Transactional
    public void crearYEnviar(Long destinatarioId, TipoNotificacion tipo,
            Map<String, Object> datos, String referenciaTipo, Long referenciaId) {

        String cuerpo = renderer.renderizar(tipo, datos);

        // El correo del destinatario se pide a la Facade de auth y no se
        // guarda aqui, para que no quede desfasado si el usuario lo cambia.
        String correo = correoDe(destinatarioId);

        // El aviso dentro de la aplicacion siempre se crea: es el registro que
        // queda en la campana. El correo solo si hay destinatario y servidor.
        entregarPor(CanalNotificacion.IN_APP, destinatarioId, tipo, cuerpo,
                referenciaTipo, referenciaId, null);

        if (correo != null && !correo.isBlank()) {
            entregarPor(CanalNotificacion.EMAIL, destinatarioId, tipo, cuerpo,
                    referenciaTipo, referenciaId, correo);
        }
    }

    @Override
    public PageResponse<NotificacionResponse> misNotificaciones(Long usuarioId, Pageable pageable) {
        return PageResponse.de(
                repositorio.findByDestinatarioIdOrderByCreatedAtDesc(usuarioId, pageable),
                mapper::aResponse);
    }

    @Override
    public long contarNoLeidas(Long usuarioId) {
        return repositorio.countByDestinatarioIdAndEstadoNot(usuarioId, EstadoNotificacion.LEIDA);
    }

    @Override
    @Transactional
    public NotificacionResponse marcarLeida(Long id, Long usuarioId) {
        // Se busca por id Y destinatario: asi un usuario no puede marcar como
        // leido un aviso ajeno conociendo su identificador.
        Notificacion notificacion = repositorio.findByIdAndDestinatarioId(id, usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        ErrorCode.NOTIFICACION_NO_ENCONTRADA, "Notificacion", id));

        notificacion.marcarLeida();
        return mapper.aResponse(notificacion);
    }

    @Override
    @Transactional
    public int reintentarPendientes() {
        List<Notificacion> pendientes = repositorio.listarPendientes(
                MAXIMO_INTENTOS, PageRequest.of(0, LOTE_REINTENTO));

        int entregadas = 0;
        for (Notificacion n : pendientes) {
            if (n.getCanal() == CanalNotificacion.EMAIL) {
                n.asignarCorreoDestino(correoDe(n.getDestinatarioId()));
            }
            if (intentarEntrega(n)) {
                entregadas++;
            }
        }

        if (!pendientes.isEmpty()) {
            log.info("Reintento de avisos: {} de {} entregados", entregadas, pendientes.size());
        }
        return entregadas;
    }

    // ---------- Apoyo ----------

    private void entregarPor(CanalNotificacion canal, Long destinatarioId, TipoNotificacion tipo,
            String cuerpo, String referenciaTipo, Long referenciaId, String correo) {

        Notificacion notificacion = new Notificacion(
                destinatarioId, tipo, canal, tipo.getAsunto(), cuerpo);

        if (referenciaTipo != null) {
            notificacion.referirA(referenciaTipo, referenciaId);
        }
        notificacion.asignarCorreoDestino(correo);

        repositorio.save(notificacion);
        intentarEntrega(notificacion);
    }

    /**
     * Entrega por el canal que la notificacion declara.
     *
     * Este metodo es el que no pregunta de que canal se trata: busca su
     * enviador y llama a enviar(). La diferencia de comportamiento entre
     * correo y aviso en aplicacion vive en cada implementacion.
     */
    private boolean intentarEntrega(Notificacion notificacion) {
        NotificacionSender enviador = enviadores.get(notificacion.getCanal());

        if (enviador == null) {
            notificacion.marcarFallida("No hay enviador para el canal " + notificacion.getCanal());
            return false;
        }
        if (!enviador.disponible()) {
            // Queda PENDIENTE sin sumar intento: el canal no esta listo, pero
            // no es culpa de este aviso en particular.
            log.debug("Canal {} no disponible; el aviso {} queda pendiente",
                    notificacion.getCanal(), notificacion.getId());
            return false;
        }

        try {
            enviador.enviar(notificacion);
            return true;
        } catch (RuntimeException e) {
            // El fallo de un aviso nunca debe tumbar la operacion que lo
            // origino: la cita ya quedo agendada.
            notificacion.marcarFallida(e.getMessage());
            log.warn("Fallo la entrega del aviso {} por {}: {}",
                    notificacion.getId(), notificacion.getCanal(), e.getMessage());
            return false;
        }
    }

    private String correoDe(Long usuarioId) {
        try {
            return authFacade.obtener(usuarioId).email();
        } catch (RuntimeException e) {
            log.warn("No se pudo obtener el correo del usuario {}", usuarioId);
            return null;
        }
    }
}
