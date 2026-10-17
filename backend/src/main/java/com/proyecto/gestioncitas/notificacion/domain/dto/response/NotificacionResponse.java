package com.proyecto.gestioncitas.notificacion.domain.dto.response;

import com.proyecto.gestioncitas.notificacion.domain.enums.CanalNotificacion;
import com.proyecto.gestioncitas.notificacion.domain.enums.EstadoNotificacion;
import com.proyecto.gestioncitas.notificacion.domain.enums.TipoNotificacion;
import java.time.Instant;

/**
 * Un aviso, tal como lo muestra la campana de notificaciones.
 *
 * referenciaTipo y referenciaId le dicen al frontend a donde navegar cuando el
 * usuario toca el aviso.
 */
public record NotificacionResponse(
        Long id,
        TipoNotificacion tipo,
        CanalNotificacion canal,
        EstadoNotificacion estado,
        String asunto,
        String mensaje,
        String referenciaTipo,
        Long referenciaId,
        boolean leida,
        Instant enviadoEn,
        Instant leidoEn,
        Instant createdAt) {
}
