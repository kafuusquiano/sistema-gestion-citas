package com.proyecto.gestioncitas.medico.exception;

import com.proyecto.gestioncitas.common.exception.ErrorCode;
import com.proyecto.gestioncitas.common.exception.ResourceNotFoundException;

/**
 * El medico no existe. Se traduce a 404.
 */
public class MedicoNoEncontradoException extends ResourceNotFoundException {

    public MedicoNoEncontradoException(Long id) {
        super(ErrorCode.MEDICO_NO_ENCONTRADO, "Medico", id);
    }

    public MedicoNoEncontradoException(String mensaje) {
        super(ErrorCode.MEDICO_NO_ENCONTRADO, mensaje);
    }

    public static MedicoNoEncontradoException porCmp(String cmp) {
        return new MedicoNoEncontradoException("No existe un medico con CMP %s".formatted(cmp));
    }

    /** Cuando un MEDICO autenticado pide su propia agenda y no tiene registro. */
    public static MedicoNoEncontradoException porUsuario(Long usuarioId) {
        return new MedicoNoEncontradoException(
                "La cuenta %d no tiene un registro de medico asociado".formatted(usuarioId));
    }
}
