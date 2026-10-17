package com.proyecto.gestioncitas.cita.scheduler;

import com.proyecto.gestioncitas.cita.domain.entity.Cita;
import com.proyecto.gestioncitas.cita.repository.CitaRepository;
import com.proyecto.gestioncitas.common.constants.AppConstants;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Marca como inasistencia las citas cuya hora paso y nadie atendio.
 *
 * Cubre la parte del objetivo especifico 4 que pide registrar las
 * inasistencias: sin este proceso, una cita a la que el paciente no se
 * presento quedaria para siempre en PROGRAMADA y el establecimiento no
 * tendria forma de medir el ausentismo.
 *
 * La tolerancia evita marcar como ausente a quien llego tarde pero fue
 * atendido: solo se consideran las citas cuya hora paso hace mas de ese
 * margen y que el medico no completo.
 *
 * Es el caso que obliga a desplegar el backend en un servicio con proceso
 * permanente. En una plataforma de funciones efimeras esta tarea no existiria.
 */
@Component
public class CitaNoAsistioScheduler {

    private static final Logger log = LoggerFactory.getLogger(CitaNoAsistioScheduler.class);

    private final CitaRepository repositorio;

    public CitaNoAsistioScheduler(CitaRepository repositorio) {
        this.repositorio = repositorio;
    }

    /**
     * Cada quince minutos. No se usa un intervalo mas corto porque el dato no
     * es urgente, ni mucho mas largo porque la agenda del dia debe reflejar la
     * realidad antes de que termine el turno.
     */
    @Scheduled(fixedDelay = 15, timeUnit = java.util.concurrent.TimeUnit.MINUTES)
    @Transactional
    @CacheEvict(value = AppConstants.CACHE_SLOTS, allEntries = true)
    public void marcarInasistencias() {
        Instant limite = Instant.now()
                .minus(AppConstants.MINUTOS_TOLERANCIA_INASISTENCIA, ChronoUnit.MINUTES);

        List<Cita> vencidas = repositorio.listarVencidasSinAtender(limite);
        if (vencidas.isEmpty()) {
            return;
        }

        // usuarioId nulo: el cambio lo hizo el sistema, no una persona. El
        // historial lo registra asi y AuditableEntity anota "sistema".
        vencidas.forEach(cita -> cita.marcarNoAsistio(null));

        log.info("Marcadas {} cita(s) como inasistencia", vencidas.size());
    }
}
