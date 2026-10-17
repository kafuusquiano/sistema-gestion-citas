package com.proyecto.gestioncitas.cita.service;

import com.proyecto.gestioncitas.cita.domain.dto.request.CancelarRequest;
import com.proyecto.gestioncitas.cita.domain.dto.request.CitaRequest;
import com.proyecto.gestioncitas.cita.domain.dto.request.ReprogramarRequest;
import com.proyecto.gestioncitas.cita.domain.dto.response.CitaResponse;
import com.proyecto.gestioncitas.cita.domain.enums.EstadoCita;
import com.proyecto.gestioncitas.common.dto.PageResponse;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.domain.Pageable;

/**
 * Programacion y seguimiento de citas. Cubre los objetivos especificos 2, 3
 * y 4 del proyecto.
 */
public interface CitaService {

    /**
     * Agenda una cita.
     *
     * Valida que el paciente pueda agendar, que el medico admita citas y que
     * el turno este libre. Publica cita.creada.
     */
    CitaResponse agendar(CitaRequest peticion, Long usuarioId);

    CitaResponse obtener(Long id);

    PageResponse<CitaResponse> buscar(Long pacienteId, Long medicoId, EstadoCita estado,
            LocalDate desde, LocalDate hasta, Pageable pageable);

    /** Citas del paciente autenticado. */
    PageResponse<CitaResponse> misCitas(Long usuarioId, Pageable pageable);

    /** Agenda del dia de un medico. */
    List<CitaResponse> agendaDelMedico(Long medicoId, LocalDate fecha);

    CitaResponse confirmar(Long id, Long usuarioId);

    /** @param esPersonal true si lo hace ADMIN o MEDICO, exentos del plazo minimo */
    CitaResponse cancelar(Long id, CancelarRequest peticion, Long usuarioId, boolean esPersonal);

    /** Mueve la cita a otra fecha. Conserva el identificador y publica el evento. */
    CitaResponse reprogramar(Long id, ReprogramarRequest peticion, Long usuarioId);

    /** Marca la atencion como realizada. Solo el MEDICO. */
    CitaResponse completar(Long id, String observaciones, Long usuarioId);

    CitaResponse marcarNoAsistio(Long id, Long usuarioId);
}
