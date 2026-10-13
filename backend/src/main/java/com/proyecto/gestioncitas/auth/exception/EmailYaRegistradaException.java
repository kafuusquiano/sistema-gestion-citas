package com.proyecto.gestioncitas.auth.exception;

import com.proyecto.gestioncitas.common.exception.ErrorCode;

/**
 * Ya existe una cuenta con ese correo.
 *
 * Aqui si se informa el conflicto, a diferencia del login. Sin este aviso la
 * persona no entiende por que no puede registrarse, y el riesgo de que
 * alguien enumere correos es menor que el de dejarla bloqueada en ventanilla
 * sin explicacion.
 *
 * El DNI duplicado se reporta con ErrorCode.DNI_YA_REGISTRADO, para que el
 * frontend sepa cual de los dos campos marcar.
 */
public class EmailYaRegistradaException extends AuthException {

    public EmailYaRegistradaException() {
        super(ErrorCode.EMAIL_YA_REGISTRADO);
    }

    public static AuthException porDniDuplicado() {
        return new AuthException(ErrorCode.DNI_YA_REGISTRADO);
    }
}
