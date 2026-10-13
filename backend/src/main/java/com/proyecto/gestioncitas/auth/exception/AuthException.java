package com.proyecto.gestioncitas.auth.exception;

import com.proyecto.gestioncitas.common.exception.BusinessException;
import com.proyecto.gestioncitas.common.exception.ErrorCode;

/**
 * Base de las excepciones del modulo auth. Hereda de BusinessException, asi
 * que GlobalExceptionHandler ya sabe traducirla a HTTP sin tratarla aparte.
 */
public class AuthException extends BusinessException {

    public AuthException(ErrorCode codigo) {
        super(codigo);
    }

    public AuthException(ErrorCode codigo, String mensaje) {
        super(codigo, mensaje);
    }
}
