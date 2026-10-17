package com.proyecto.gestioncitas.cita.mapper;

import com.proyecto.gestioncitas.cita.domain.dto.response.CitaResponse;
import com.proyecto.gestioncitas.cita.domain.entity.Cita;
import com.proyecto.gestioncitas.cita.domain.entity.CitaHistorialEstado;
import com.proyecto.gestioncitas.common.constants.AppConstants;
import com.proyecto.gestioncitas.common.util.DateUtils;
import com.proyecto.gestioncitas.medico.api.MedicoFacade;
import com.proyecto.gestioncitas.paciente.api.PacienteFacade;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * Arma la respuesta de una cita resolviendo los datos de paciente y medico.
 *
 * No usa MapStruct a diferencia de los otros modulos, porque la conversion no
 * es un copiado de campos: hay que consultar dos Facades y calcular tres
 * valores derivados. Un mapper generado no podria hacerlo sin recibir las
 * dependencias igual, y el resultado seria menos legible.
 *
 * Los nombres llegan por las Facades y nunca importando las entidades Paciente
 * o Medico, que son internas de sus modulos (instruccion M1).
 */
@Component
public class CitaMapper {

    private final PacienteFacade pacienteFacade;
    private final MedicoFacade medicoFacade;

    public CitaMapper(PacienteFacade pacienteFacade, MedicoFacade medicoFacade) {
        this.pacienteFacade = pacienteFacade;
        this.medicoFacade = medicoFacade;
    }

    public CitaResponse aResponse(Cita cita) {
        var paciente = pacienteFacade.obtenerResumen(cita.getPacienteId());
        var medico = medicoFacade.obtenerResumen(cita.getMedicoId());

        return new CitaResponse(
                cita.getId(),

                paciente.id(),
                paciente.nombreCompleto(),
                paciente.numeroHistoria(),

                medico.id(),
                medico.nombreCompleto(),
                medico.especialidad(),

                DateUtils.aLocal(cita.getFechaHora()),
                cita.getDuracionMinutos(),
                cita.getEstado(),
                cita.getTipo(),
                cita.getMotivo(),
                cita.getObservaciones(),
                cita.getMotivoCancelacion(),

                // Le dice al frontend que botones habilitar, en lugar de
                // duplicar las reglas de estado en el cliente.
                cita.getEstado().transicionesPosibles(),
                puedeCancelarse(cita),

                historialDe(cita),
                cita.getCreatedAt());
    }

    public List<CitaResponse> aResponses(List<Cita> citas) {
        return citas.stream().map(this::aResponse).toList();
    }

    /**
     * Refleja la misma regla que aplica CitaEstadoValidator, para que el boton
     * no aparezca habilitado si la operacion va a ser rechazada.
     */
    private boolean puedeCancelarse(Cita cita) {
        if (!cita.estaVigente()) {
            return false;
        }
        if (cita.getTipo().requierePersonalAdmision()) {
            return true;
        }
        return cita.horasHastaLaCita() >= AppConstants.HORAS_MINIMAS_CANCELACION;
    }

    private List<CitaResponse.CambioEstadoResponse> historialDe(Cita cita) {
        return cita.getHistorial().stream()
                .sorted(Comparator.comparing(CitaHistorialEstado::getCreatedAt,
                        Comparator.nullsLast(Comparator.naturalOrder())))
                .map(h -> new CitaResponse.CambioEstadoResponse(
                        h.getEstadoAnterior(),
                        h.getEstadoNuevo(),
                        aLocalONull(h.getFechaHoraAnterior()),
                        aLocalONull(h.getFechaHoraNueva()),
                        h.getMotivo(),
                        h.getCreatedAt()))
                .toList();
    }

    private java.time.LocalDateTime aLocalONull(Instant instante) {
        return instante == null ? null : DateUtils.aLocal(instante);
    }
}
