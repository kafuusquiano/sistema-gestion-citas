package com.proyecto.gestioncitas.cita.service.impl;

import com.proyecto.gestioncitas.cita.domain.dto.response.SlotDisponibleResponse;
import com.proyecto.gestioncitas.cita.domain.entity.Cita;
import com.proyecto.gestioncitas.cita.repository.CitaRepository;
import com.proyecto.gestioncitas.cita.service.DisponibilidadService;
import com.proyecto.gestioncitas.common.constants.AppConstants;
import com.proyecto.gestioncitas.common.util.DateUtils;
import com.proyecto.gestioncitas.medico.api.MedicoFacade;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Proyecta la plantilla semanal del medico sobre una fecha concreta y descuenta
 * lo que ya no esta libre.
 *
 * El algoritmo tiene cuatro pasos:
 *   1. Pedir a MedicoFacade las franjas de atencion de esa fecha. La Facade ya
 *      devuelve lista vacia si ese dia hay una excepcion, de modo que aqui no
 *      hace falta conocer vacaciones ni feriados.
 *   2. Partir cada franja en turnos del tamano que indique la propia franja.
 *   3. Leer las citas vigentes del medico ese dia y marcar sus horas.
 *   4. Descartar los turnos que ya pasaron, porque no sirve ofrecer las nueve
 *      de la manana a las once.
 *
 * El modulo nunca toca HorarioRepository ni la entidad Horario: todo lo que
 * sabe del medico entra por su Facade (instruccion M1).
 */
@Service
@Transactional(readOnly = true)
public class DisponibilidadServiceImpl implements DisponibilidadService {

    private static final Logger log = LoggerFactory.getLogger(DisponibilidadServiceImpl.class);

    private final MedicoFacade medicoFacade;
    private final CitaRepository citaRepository;

    public DisponibilidadServiceImpl(MedicoFacade medicoFacade, CitaRepository citaRepository) {
        this.medicoFacade = medicoFacade;
        this.citaRepository = citaRepository;
    }

    @Override
    @Cacheable(value = AppConstants.CACHE_SLOTS, key = "#medicoId + '-' + #fecha")
    public List<SlotDisponibleResponse> obtenerSlots(Long medicoId, LocalDate fecha) {
        List<MedicoFacade.FranjaAtencion> franjas = medicoFacade.obtenerFranjas(medicoId, fecha);
        if (franjas.isEmpty()) {
            log.debug("El medico {} no atiende el {}", medicoId, fecha);
            return List.of();
        }

        Set<Instant> ocupados = horasOcupadas(medicoId, fecha);
        Instant ahora = Instant.now();
        List<SlotDisponibleResponse> slots = new ArrayList<>();

        for (MedicoFacade.FranjaAtencion franja : franjas) {
            LocalTime hora = franja.horaInicio();

            // Se avanza turno a turno hasta que el siguiente ya no cabe
            // completo dentro de la franja.
            while (!hora.plusMinutes(franja.duracionSlotMinutos()).isAfter(franja.horaFin())) {
                LocalDateTime fechaHoraLocal = LocalDateTime.of(fecha, hora);
                Instant instante = DateUtils.aInstante(fechaHoraLocal);

                if (instante.isAfter(ahora)) {
                    slots.add(ocupados.contains(instante)
                            ? SlotDisponibleResponse.ocupado(fechaHoraLocal, franja.duracionSlotMinutos())
                            : SlotDisponibleResponse.libre(fechaHoraLocal, franja.duracionSlotMinutos()));
                }

                hora = hora.plusMinutes(franja.duracionSlotMinutos());
            }
        }

        log.debug("Medico {} el {}: {} turnos, {} libres", medicoId, fecha, slots.size(),
                slots.stream().filter(SlotDisponibleResponse::disponible).count());
        return slots;
    }

    @Override
    public List<SlotDisponibleResponse> obtenerSlotsLibres(Long medicoId, LocalDate fecha) {
        return obtenerSlots(medicoId, fecha).stream()
                .filter(SlotDisponibleResponse::disponible)
                .toList();
    }

    @Override
    public boolean esHorarioDeAtencion(Long medicoId, Instant fechaHora) {
        LocalDateTime local = DateUtils.aLocal(fechaHora);

        // No se usa obtenerSlots porque esa lista viene de la cache y podria
        // estar desactualizada justo en el momento de guardar. Aqui se
        // recalcula contra las franjas vigentes.
        return medicoFacade.obtenerFranjas(medicoId, local.toLocalDate()).stream()
                .anyMatch(franja -> caeEnInicioDeTurno(local.toLocalTime(), franja));
    }

    @Override
    public boolean esSlotValido(Long medicoId, Instant fechaHora) {
        return esHorarioDeAtencion(medicoId, fechaHora)
                && !citaRepository.existeCitaVigenteEnSlot(medicoId, fechaHora);
    }

    @Override
    public int duracionSlotDelMedico(Long medicoId, LocalDate fecha) {
        return medicoFacade.obtenerFranjas(medicoId, fecha).stream()
                .findFirst()
                .map(MedicoFacade.FranjaAtencion::duracionSlotMinutos)
                // Si ese dia no atiende, se devuelve la duracion que sugiere
                // su tipo: 20 minutos un general, 30 un especialista.
                .orElseGet(() -> medicoFacade.obtenerResumen(medicoId).duracionConsultaMinutos());
    }

    // ---------- Apoyo ----------

    /** Horas que ya tienen una cita vigente, en el dia indicado. */
    private Set<Instant> horasOcupadas(Long medicoId, LocalDate fecha) {
        Instant desde = DateUtils.aInstante(fecha.atStartOfDay());
        Instant hasta = DateUtils.aInstante(fecha.plusDays(1).atStartOfDay());

        Set<Instant> ocupados = new HashSet<>();
        for (Cita cita : citaRepository.listarVigentesDelMedicoEnRango(medicoId, desde, hasta)) {
            ocupados.add(cita.getFechaHora());
        }
        return ocupados;
    }

    /**
     * true si la hora cae dentro de la franja y coincide exactamente con el
     * inicio de un turno. Sin esta segunda condicion se podria agendar a las
     * 08:07 en una franja de turnos de veinte minutos.
     */
    private boolean caeEnInicioDeTurno(LocalTime hora, MedicoFacade.FranjaAtencion franja) {
        if (hora.isBefore(franja.horaInicio()) || !hora.isBefore(franja.horaFin())) {
            return false;
        }
        long minutosDesdeInicio = java.time.Duration
                .between(franja.horaInicio(), hora).toMinutes();
        if (minutosDesdeInicio % franja.duracionSlotMinutos() != 0) {
            return false;
        }
        // El turno debe caber completo antes del fin de la franja.
        return !hora.plusMinutes(franja.duracionSlotMinutos()).isAfter(franja.horaFin());
    }
}
