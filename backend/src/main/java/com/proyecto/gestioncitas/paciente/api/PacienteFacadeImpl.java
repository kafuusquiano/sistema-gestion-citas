package com.proyecto.gestioncitas.paciente.api;

import com.proyecto.gestioncitas.paciente.domain.entity.Paciente;
import com.proyecto.gestioncitas.paciente.exception.PacienteNoEncontradoException;
import com.proyecto.gestioncitas.paciente.repository.PacienteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementacion de la fachada. Traduce la entidad al record publico, de modo
 * que Paciente nunca sale del modulo.
 */
@Service
@Transactional(readOnly = true)
public class PacienteFacadeImpl implements PacienteFacade {

    private final PacienteRepository repositorio;

    public PacienteFacadeImpl(PacienteRepository repositorio) {
        this.repositorio = repositorio;
    }

    @Override
    public boolean existe(Long pacienteId) {
        return pacienteId != null && repositorio.existsById(pacienteId);
    }

    @Override
    public boolean puedeAgendar(Long pacienteId) {
        if (pacienteId == null) {
            return false;
        }
        return repositorio.findById(pacienteId)
                .map(Paciente::puedeAgendar)
                .orElse(false);
    }

    @Override
    public ResumenPaciente obtenerResumen(Long pacienteId) {
        Paciente paciente = repositorio.findById(pacienteId)
                .orElseThrow(() -> new PacienteNoEncontradoException(pacienteId));

        return new ResumenPaciente(
                paciente.getId(),
                paciente.getNumeroHistoria(),
                paciente.getNumeroDocumento(),
                paciente.nombreCompleto(),
                paciente.edad(),
                paciente.getSexo().name(),
                paciente.getEmail(),
                paciente.puedeAgendar());
    }

    @Override
    public Long obtenerIdPorUsuario(Long usuarioId) {
        if (usuarioId == null) {
            return null;
        }
        return repositorio.findByUsuarioId(usuarioId)
                .map(Paciente::getId)
                .orElse(null);
    }
}
