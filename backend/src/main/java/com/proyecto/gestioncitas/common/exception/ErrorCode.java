package com.proyecto.gestioncitas.common.exception;

import org.springframework.http.HttpStatus;

/**
 * Catalogo de errores del sistema. Cada codigo fija su estado HTTP y su
 * mensaje por defecto en espanol (instruccion G4).
 *
 * Los modulos de dominio reutilizan estos codigos en lugar de inventar textos
 * sueltos, para que el frontend pueda reaccionar al codigo y no al mensaje.
 */
public enum ErrorCode {

    // ---------- Genericos ----------
    RECURSO_NO_ENCONTRADO(HttpStatus.NOT_FOUND, "El recurso solicitado no existe"),
    DATOS_INVALIDOS(HttpStatus.BAD_REQUEST, "Los datos enviados no son validos"),
    VALIDACION_FALLIDA(HttpStatus.UNPROCESSABLE_ENTITY, "Hay campos con errores de validacion"),
    CONFLICTO_DE_ESTADO(HttpStatus.CONFLICT, "La operacion no es valida en el estado actual del recurso"),
    ERROR_INTERNO(HttpStatus.INTERNAL_SERVER_ERROR, "Ocurrio un error inesperado"),

    // ---------- Autenticacion y autorizacion ----------
    NO_AUTENTICADO(HttpStatus.UNAUTHORIZED, "Debe iniciar sesion para realizar esta accion"),
    CREDENCIALES_INVALIDAS(HttpStatus.UNAUTHORIZED, "El correo o la contrasena son incorrectos"),
    TOKEN_INVALIDO(HttpStatus.UNAUTHORIZED, "El token es invalido o esta malformado"),
    TOKEN_EXPIRADO(HttpStatus.UNAUTHORIZED, "La sesion expiro, vuelva a iniciar sesion"),
    TOKEN_REVOCADO(HttpStatus.UNAUTHORIZED, "La sesion fue cerrada"),
    SIN_PERMISOS(HttpStatus.FORBIDDEN, "No tiene permisos para realizar esta accion"),
    USUARIO_BLOQUEADO(HttpStatus.FORBIDDEN, "La cuenta esta bloqueada, contacte al administrador"),
    EMAIL_YA_REGISTRADO(HttpStatus.CONFLICT, "El correo ya esta registrado"),
    DNI_YA_REGISTRADO(HttpStatus.CONFLICT, "El DNI ya esta registrado"),

    // ---------- Pacientes y medicos ----------
    PACIENTE_NO_ENCONTRADO(HttpStatus.NOT_FOUND, "El paciente no existe"),
    MEDICO_NO_ENCONTRADO(HttpStatus.NOT_FOUND, "El medico no existe"),
    ESPECIALIDAD_NO_ENCONTRADA(HttpStatus.NOT_FOUND, "La especialidad no existe"),
    HORARIO_INVALIDO(HttpStatus.BAD_REQUEST, "El horario indicado no es valido"),
    HORARIO_SUPERPUESTO(HttpStatus.CONFLICT, "El horario se cruza con otro ya registrado"),

    // ---------- Citas ----------
    CITA_NO_ENCONTRADA(HttpStatus.NOT_FOUND, "La cita no existe"),
    SLOT_NO_DISPONIBLE(HttpStatus.CONFLICT, "El horario elegido ya fue reservado"),
    TRANSICION_ESTADO_INVALIDA(HttpStatus.CONFLICT, "La cita no puede pasar a ese estado"),
    FUERA_DE_HORARIO_ATENCION(HttpStatus.BAD_REQUEST, "La hora elegida esta fuera del horario de atencion"),
    CANCELACION_FUERA_DE_PLAZO(HttpStatus.CONFLICT, "La cita ya no puede cancelarse por el plazo minimo"),
    FECHA_EN_EL_PASADO(HttpStatus.BAD_REQUEST, "No se puede agendar una cita en una fecha pasada"),

    // ---------- Historial clinico ----------
    HISTORIAL_NO_ENCONTRADO(HttpStatus.NOT_FOUND, "El historial clinico no existe"),
    ATENCION_NO_ENCONTRADA(HttpStatus.NOT_FOUND, "La atencion no existe"),

    // ---------- Laboratorio ----------
    ORDEN_NO_ENCONTRADA(HttpStatus.NOT_FOUND, "La orden de laboratorio no existe"),
    RESULTADO_NO_ENCONTRADO(HttpStatus.NOT_FOUND, "El resultado no existe"),
    EXAMEN_NO_ENCONTRADO(HttpStatus.NOT_FOUND, "El examen no esta en el catalogo"),

    // ---------- Notificaciones ----------
    NOTIFICACION_NO_ENCONTRADA(HttpStatus.NOT_FOUND, "La notificacion no existe"),
    ENVIO_NOTIFICACION_FALLIDO(HttpStatus.INTERNAL_SERVER_ERROR, "No se pudo enviar la notificacion");

    private final HttpStatus estado;
    private final String mensaje;

    ErrorCode(HttpStatus estado, String mensaje) {
        this.estado = estado;
        this.mensaje = mensaje;
    }

    public HttpStatus getEstado() {
        return estado;
    }

    public String getMensaje() {
        return mensaje;
    }
}
