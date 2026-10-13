package com.proyecto.gestioncitas.auth.exception;

import com.proyecto.gestioncitas.common.exception.ErrorCode;

/**
 * Correo o contrasena incorrectos.
 *
 * El mensaje es deliberadamente ambiguo y no distingue si el correo existe o
 * si la contrasena esta mal. Decirlo permitiria averiguar que correos estan
 * registrados en el establecimiento probandolos uno por uno.
 */
public class CredencialesInvalidasException extends AuthException {

    public CredencialesInvalidasException() {
        super(ErrorCode.CREDENCIALES_INVALIDAS);
    }
}
