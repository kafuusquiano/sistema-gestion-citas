package com.proyecto.gestioncitas.paciente.exception;

import com.proyecto.gestioncitas.common.exception.BusinessException;

public class PacienteNotFoundException extends BusinessException {
    public PacienteNotFoundException(String id) {
        super("PACIENTE_NO_ENCONTRADO", "Paciente no encontrado con id: " + id);
    }
}
