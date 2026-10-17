package com.proyecto.gestioncitas.cita.service;

import com.proyecto.gestioncitas.cita.domain.dto.response.SlotDisponibleResponse;
import java.time.LocalDate;
import java.util.List;

/**
 * Calculo de turnos libres (objetivo especifico 2: evitar conflictos de
 * horario).
 */
public interface DisponibilidadService {

    /**
     * Turnos de un medico en una fecha.
     *
     * El calculo parte de la plantilla semanal del medico, le resta las
     * excepciones de ese dia y le quita las citas ya reservadas. Devuelve
     * tambien los ocupados, marcados como no disponibles, para que el
     * calendario pueda mostrarlos en gris.
     *
     * Cacheado 5 minutos (instruccion M4). La cache se descarta cuando cambia
     * el horario del medico o cuando se mueve una cita.
     */
    List<SlotDisponibleResponse> obtenerSlots(Long medicoId, LocalDate fecha);

    /** Solo los turnos libres, para el selector al agendar. */
    List<SlotDisponibleResponse> obtenerSlotsLibres(Long medicoId, LocalDate fecha);

    /**
     * true si la hora cae dentro de una franja del medico y coincide con el
     * inicio de un turno, sin mirar si esta ocupada.
     *
     * Se consulta antes que esSlotValido para poder distinguir dos fallos que
     * el usuario vive de forma muy distinta: pedir una hora en la que el
     * medico no atiende, y pedir una hora valida que otro paciente ya tomo.
     */
    boolean esHorarioDeAtencion(Long medicoId, java.time.Instant fechaHora);

    /**
     * Comprobacion previa a guardar una cita.
     *
     * Verifica que la hora caiga dentro de una franja del medico, coincida con
     * el inicio de un turno y no este tomada. No es garantia suficiente contra
     * la doble reserva simultanea: de eso se encarga el indice unico de la
     * base de datos.
     */
    boolean esSlotValido(Long medicoId, java.time.Instant fechaHora);

    /** Duracion del turno del medico, que depende de su tipo. */
    int duracionSlotDelMedico(Long medicoId, LocalDate fecha);
}
