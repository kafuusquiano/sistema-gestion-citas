package com.proyecto.gestioncitas.paciente.exception;

import com.proyecto.gestioncitas.common.exception.ErrorCode;
import com.proyecto.gestioncitas.common.exception.ResourceNotFoundException;

/**
 * El paciente no existe. Se traduce a 404.
 */
public class PacienteNoEncontradoException extends ResourceNotFoundException {

    public PacienteNoEncontradoException(Long id) {
        super(ErrorCode.PACIENTE_NO_ENCONTRADO, "Paciente", id);
    }

    public PacienteNoEncontradoException(String mensaje) {
        super(ErrorCode.PACIENTE_NO_ENCONTRADO, mensaje);
    }

    /** Cuando se busca por documento y no hay ficha. */
    public static PacienteNoEncontradoException porDocumento(String numeroDocumento) {
        return new PacienteNoEncontradoException(
                "No existe un paciente con documento %s".formatted(numeroDocumento));
    }

    /**
     * Cuando el usuario autenticado pide /me pero no tiene ficha. Pasa con un
     * ADMIN o un MEDICO, que tienen cuenta pero no son pacientes.
     */
    public static PacienteNoEncontradoException porUsuario(Long usuarioId) {
        return new PacienteNoEncontradoException(
                "La cuenta %d no tiene una ficha de paciente asociada".formatted(usuarioId));
    }
}
