package com.proyecto.gestioncitas.notificacion.scheduler;

import com.proyecto.gestioncitas.notificacion.service.NotificacionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Reintenta las entregas que quedaron pendientes o fallidas.
 *
 * Un fallo del servidor de correo suele ser temporal, asi que un aviso que no
 * salio en el primer intento probablemente salga en el siguiente. Tras cinco
 * fallos se deja de insistir, porque a esas alturas la direccion suele ser
 * invalida y seguir intentando solo gasta recursos.
 *
 * El recordatorio a 24 horas de la cita queda pendiente de que el modulo cita
 * exponga por su Facade la consulta de citas proximas. El evento y el tipo de
 * notificacion ya existen para cuando se agregue.
 */
@Component
public class RecordatorioScheduler {

    private static final Logger log = LoggerFactory.getLogger(RecordatorioScheduler.class);

    private final NotificacionService notificacionService;

    public RecordatorioScheduler(NotificacionService notificacionService) {
        this.notificacionService = notificacionService;
    }

    /**
     * Cada diez minutos. Lo bastante seguido para que un corte breve del
     * servidor de correo no retrase mucho los avisos, y lo bastante espaciado
     * para no castigar los 0.1 CPU del plan gratuito de Render.
     */
    @Scheduled(fixedDelay = 10, timeUnit = java.util.concurrent.TimeUnit.MINUTES)
    public void reintentarEnviosPendientes() {
        int entregados = notificacionService.reintentarPendientes();
        if (entregados > 0) {
            log.info("Reintento: {} aviso(s) entregado(s)", entregados);
        }
    }
}
