package com.proyecto.gestioncitas.cita.api;

import com.proyecto.gestioncitas.cita.domain.entity.Cita;
import com.proyecto.gestioncitas.cita.domain.enums.EstadoCita;
import com.proyecto.gestioncitas.cita.repository.CitaRepository;
import com.proyecto.gestioncitas.common.exception.ErrorCode;
import com.proyecto.gestioncitas.common.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementacion de la fachada. Traduce la entidad al record publico, de modo
 * que Cita nunca sale del modulo.
 */
@Service
@Transactional(readOnly = true)
public class CitaFacadeImpl implements CitaFacade {

    private final CitaRepository repositorio;

    public CitaFacadeImpl(CitaRepository repositorio) {
        this.repositorio = repositorio;
    }

    @Override
    public boolean existe(Long citaId) {
        return citaId != null && repositorio.existsById(citaId);
    }

    @Override
    public ResumenCita obtenerResumen(Long citaId) {
        Cita cita = repositorio.findById(citaId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        ErrorCode.CITA_NO_ENCONTRADA, "Cita", citaId));

        return new ResumenCita(
                cita.getId(),
                cita.getPacienteId(),
                cita.getMedicoId(),
                cita.getFechaHora(),
                cita.getEstado().name(),
                cita.estaVigente());
    }

    @Override
    public long contarVigentesDelPaciente(Long pacienteId) {
        return repositorio.countByPacienteIdAndEstado(pacienteId, EstadoCita.PROGRAMADA)
             + repositorio.countByPacienteIdAndEstado(pacienteId, EstadoCita.CONFIRMADA);
    }
}
