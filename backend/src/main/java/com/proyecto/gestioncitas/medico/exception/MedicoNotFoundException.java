package com.proyecto.gestioncitas.medico.exception;

import com.proyecto.gestioncitas.common.exception.BusinessException;

public class MedicoNotFoundException extends BusinessException {
    public MedicoNotFoundException(String id) {
        super("MEDICO_NO_ENCONTRADO", "Médico no encontrado con id: " + id);
    }
}
