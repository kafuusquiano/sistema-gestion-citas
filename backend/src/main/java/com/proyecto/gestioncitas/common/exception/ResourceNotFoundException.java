package com.proyecto.gestioncitas.common.exception;

/**
 * Recurso inexistente. Siempre se traduce a 404.
 */
public class ResourceNotFoundException extends BusinessException {

    public ResourceNotFoundException(ErrorCode codigo) {
        super(codigo);
    }

    public ResourceNotFoundException(ErrorCode codigo, String mensaje) {
        super(codigo, mensaje);
    }

    /**
     * Atajo para el caso habitual de buscar por identificador.
     *
     * Ejemplo: new ResourceNotFoundException(ErrorCode.PACIENTE_NO_ENCONTRADO, "Paciente", id)
     * produce "Paciente con id 42 no existe".
     */
    public ResourceNotFoundException(ErrorCode codigo, String recurso, Object id) {
        super(codigo, "%s con id %s no existe".formatted(recurso, id));
    }
}
