package com.proyecto.gestioncitas.notificacion.service;

import com.proyecto.gestioncitas.common.dto.PageResponse;
import com.proyecto.gestioncitas.notificacion.domain.dto.response.NotificacionResponse;
import com.proyecto.gestioncitas.notificacion.domain.enums.TipoNotificacion;
import java.util.Map;
import org.springframework.data.domain.Pageable;

/**
 * Creacion, entrega y consulta de avisos.
 */
public interface NotificacionService {

    /**
     * Crea el aviso, lo renderiza y lo entrega por los canales que
     * correspondan.
     *
     * @param destinatarioId id del usuario que recibe el aviso
     * @param datos variables de la plantilla; debe incluir al menos "nombre"
     * @param referenciaTipo a que recurso apunta, por ejemplo "CITA"
     * @param referenciaId identificador de ese recurso
     */
    void crearYEnviar(Long destinatarioId, TipoNotificacion tipo, Map<String, Object> datos,
            String referenciaTipo, Long referenciaId);

    PageResponse<NotificacionResponse> misNotificaciones(Long usuarioId, Pageable pageable);

    long contarNoLeidas(Long usuarioId);

    NotificacionResponse marcarLeida(Long id, Long usuarioId);

    /** Vuelve a intentar las entregas que quedaron pendientes o fallidas. */
    int reintentarPendientes();
}
