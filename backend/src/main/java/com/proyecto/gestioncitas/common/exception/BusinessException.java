package com.proyecto.gestioncitas.common.exception;

/**
 * Excepcion base de toda regla de negocio incumplida.
 *
 * Las excepciones de cada modulo extienden esta clase, de modo que
 * GlobalExceptionHandler las traduce a HTTP en un solo lugar. Lleva el
 * ErrorCode, que es quien decide el estado HTTP y el mensaje por defecto.
 */
public class BusinessException extends RuntimeException {

    private final ErrorCode codigo;

    public BusinessException(ErrorCode codigo) {
        super(codigo.getMensaje());
        this.codigo = codigo;
    }

    /** Cuando se necesita un mensaje mas especifico que el del catalogo. */
    public BusinessException(ErrorCode codigo, String mensaje) {
        super(mensaje);
        this.codigo = codigo;
    }

    public BusinessException(ErrorCode codigo, String mensaje, Throwable causa) {
        super(mensaje, causa);
        this.codigo = codigo;
    }

    public ErrorCode getCodigo() {
        return codigo;
    }
}
