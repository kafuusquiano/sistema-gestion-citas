package com.proyecto.gestioncitas.auth.api;

import com.proyecto.gestioncitas.auth.domain.enums.Rol;

/**
 * Unica puerta de entrada al modulo auth desde otros modulos (instruccion M1).
 *
 * Los modulos paciente y medico la usan para crear la cuenta de acceso de la
 * persona que registran. Nunca tocan UsuarioRepository ni la entidad Usuario.
 *
 * Los tipos que cruzan esta frontera se declaran aqui dentro, como records
 * anidados: asi el contrato publico cabe en un archivo y ningun modulo
 * externo necesita importar un DTO interno de auth.
 */
public interface AuthFacade {

    /** true si la cuenta existe, sin importar su estado. */
    boolean existe(Long usuarioId);

    /**
     * Datos minimos de una cuenta.
     *
     * @throws com.proyecto.gestioncitas.common.exception.ResourceNotFoundException
     *         si no existe
     */
    UsuarioPublico obtener(Long usuarioId);

    /**
     * Crea la cuenta de acceso de un medico o un administrador.
     *
     * El registro de pacientes no pasa por aqui: va por /api/auth/register,
     * que publica usuario.registrado y deja que el modulo paciente cree la
     * ficha al escucharlo.
     *
     * @return id de la cuenta creada
     */
    Long crearCuenta(NuevaCuenta datos);

    /** Lo que otro modulo puede ver de una cuenta. Sin hash ni intentos fallidos. */
    record UsuarioPublico(Long id, String email, String dni, Rol rol, boolean activo) {
    }

    /** Datos para dar de alta una cuenta desde otro modulo. */
    record NuevaCuenta(String email, String dni, String passwordEnClaro, Rol rol) {
    }
}
