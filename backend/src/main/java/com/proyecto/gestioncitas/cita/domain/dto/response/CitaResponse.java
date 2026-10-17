package com.proyecto.gestioncitas.cita.domain.dto.response;

import com.proyecto.gestioncitas.cita.domain.enums.EstadoCita;
import com.proyecto.gestioncitas.cita.domain.enums.TipoCita;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

/**
 * Una cita, con los datos de paciente y medico ya resueltos.
 *
 * El modulo cita no conoce esas entidades: los nombres los pide a
 * PacienteFacade y a MedicoFacade al armar la respuesta. Asi el frontend
 * muestra la cita completa con una sola llamada, sin tener que consultar
 * despues quien es el paciente y quien el medico.
 *
 * transicionesPosibles le dice al frontend que botones habilitar, en lugar de
 * duplicar las reglas de estado en el cliente.
 */
public record CitaResponse(
        Long id,

        Long pacienteId,
        String pacienteNombre,
        String pacienteNumeroHistoria,

        Long medicoId,
        String medicoNombre,
        String medicoEspecialidad,

        /** En la zona horaria del establecimiento. */
        LocalDateTime fechaHora,

        int duracionMinutos,
        EstadoCita estado,
        TipoCita tipo,
        String motivo,
        String observaciones,
        String motivoCancelacion,

        /** Estados a los que la cita puede pasar desde su estado actual. */
        Set<EstadoCita> transicionesPosibles,

        boolean puedeCancelarse,

        List<CambioEstadoResponse> historial,

        Instant createdAt) {

    /** Un asiento del historial de la cita. */
    public record CambioEstadoResponse(
            EstadoCita estadoAnterior,
            EstadoCita estadoNuevo,
            LocalDateTime fechaHoraAnterior,
            LocalDateTime fechaHoraNueva,
            String motivo,
            Instant ocurridoEn) {
    }
}
