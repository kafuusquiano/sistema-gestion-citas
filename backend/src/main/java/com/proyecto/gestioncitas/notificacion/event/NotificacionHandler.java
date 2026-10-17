package com.proyecto.gestioncitas.notificacion.event;

import com.proyecto.gestioncitas.common.util.DateUtils;
import com.proyecto.gestioncitas.notificacion.domain.enums.TipoNotificacion;
import com.proyecto.gestioncitas.notificacion.service.NotificacionService;
import com.proyecto.gestioncitas.paciente.api.PacienteFacade;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Logica comun a los consumidores de eventos.
 *
 * Los listeners solo traducen un evento a una llamada de este handler. Tener
 * la preparacion de datos aqui evita repetir en cada listener la resolucion
 * del paciente y el formateo de fechas.
 *
 * Un detalle importante: el aviso va dirigido al USUARIO, mientras que los
 * eventos de cita traen el id del PACIENTE. La traduccion entre ambos se hace
 * con PacienteFacade, porque un paciente registrado por ventanilla puede no
 * tener cuenta, y en ese caso no hay a quien avisar.
 */
@Component
public class NotificacionHandler {

    private static final Logger log = LoggerFactory.getLogger(NotificacionHandler.class);

    private static final String REFERENCIA_CITA = "CITA";

    private final NotificacionService notificacionService;
    private final PacienteFacade pacienteFacade;

    public NotificacionHandler(NotificacionService notificacionService,
            PacienteFacade pacienteFacade) {
        this.notificacionService = notificacionService;
        this.pacienteFacade = pacienteFacade;
    }

    /** Aviso de bienvenida tras el registro. Aqui el destinatario ya es el usuario. */
    public void bienvenida(Long usuarioId, String nombreCompleto) {
        Map<String, Object> datos = new HashMap<>();
        datos.put("nombre", nombreCompleto);
        datos.put("establecimiento", "Centro de Salud Acapulco");

        notificacionService.crearYEnviar(
                usuarioId, TipoNotificacion.BIENVENIDA, datos, null, null);
    }

    /** Aviso sobre una cita. Resuelve el usuario a partir del paciente. */
    public void sobreCita(TipoNotificacion tipo, Long pacienteId, Long citaId,
            Instant fechaHora, String detalle) {

        Long usuarioId = usuarioDelPaciente(pacienteId);
        if (usuarioId == null) {
            log.debug("El paciente {} no tiene cuenta: no hay a quien avisar", pacienteId);
            return;
        }

        var resumen = pacienteFacade.obtenerResumen(pacienteId);

        Map<String, Object> datos = new HashMap<>();
        datos.put("nombre", resumen.nombreCompleto());
        datos.put("fecha", DateUtils.formatearFechaLarga(DateUtils.aLocal(fechaHora).toLocalDate()));
        datos.put("hora", DateUtils.formatearHora(DateUtils.aLocal(fechaHora).toLocalTime()));
        datos.put("detalle", detalle == null ? "" : detalle);
        datos.put("establecimiento", "Centro de Salud Acapulco");

        notificacionService.crearYEnviar(usuarioId, tipo, datos, REFERENCIA_CITA, citaId);
    }

    private Long usuarioDelPaciente(Long pacienteId) {
        try {
            return pacienteFacade.obtenerUsuarioIdPorPaciente(pacienteId);
        } catch (RuntimeException e) {
            log.warn("No se pudo resolver el usuario del paciente {}", pacienteId);
            return null;
        }
    }
}
