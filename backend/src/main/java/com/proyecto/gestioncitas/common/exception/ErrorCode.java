package com.proyecto.gestioncitas.common.exception;

public final class ErrorCode {
    public static final String CREDENCIALES_INVALIDAS = "CREDENCIALES_INVALIDAS";
    public static final String TOKEN_INVALIDO         = "TOKEN_INVALIDO";
    public static final String TOKEN_EXPIRADO         = "TOKEN_EXPIRADO";
    public static final String EMAIL_YA_REGISTRADO    = "EMAIL_YA_REGISTRADO";
    public static final String SLOT_NO_DISPONIBLE     = "SLOT_NO_DISPONIBLE";
    public static final String CITA_NO_CANCELABLE     = "CITA_NO_CANCELABLE";
    public static final String VALIDACION_FALLIDA     = "VALIDACION_FALLIDA";
    private ErrorCode() {}
}
