package com.proyecto.gestioncitas.cita.event.consumers;

import com.proyecto.gestioncitas.common.constants.AppConstants;
import com.proyecto.gestioncitas.medico.event.HorarioActualizadoEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * Descarta los cupos calculados cuando cambia el horario de un medico.
 *
 * Sin esto, DisponibilidadService seguiria ofreciendo los turnos del horario
 * anterior durante los cinco minutos de vigencia de la cache, y un paciente
 * podria reservar una hora en la que el medico ya no atiende.
 *
 * Se invalida la cache completa y no solo la del medico afectado porque la
 * clave incluye la fecha: habria que recorrer todas las fechas posibles de
 * ese medico para borrarlas una por una.
 */
@Component
public class HorarioActualizadoListener {

    private static final Logger log = LoggerFactory.getLogger(HorarioActualizadoListener.class);

    @EventListener
    @CacheEvict(value = AppConstants.CACHE_SLOTS, allEntries = true)
    public void alActualizarseUnHorario(HorarioActualizadoEvent evento) {
        log.debug("Cache de disponibilidad invalidada por el medico {}: {}",
                evento.medicoId(), evento.motivo());
    }
}
