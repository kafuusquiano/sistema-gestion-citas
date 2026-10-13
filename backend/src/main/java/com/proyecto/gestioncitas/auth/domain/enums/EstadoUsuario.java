package com.proyecto.gestioncitas.auth.domain.enums;

/**
 * Estado de la cuenta. Solo ACTIVO permite iniciar sesion.
 */
public enum EstadoUsuario {

    ACTIVO,

    /** Dado de baja por un administrador. */
    INACTIVO,

    /** Bloqueado automaticamente tras varios intentos fallidos de login. */
    BLOQUEADO;

    public boolean puedeIniciarSesion() {
        return this == ACTIVO;
    }
}
