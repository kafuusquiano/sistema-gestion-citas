package com.proyecto.gestioncitas.auth.domain.dto.response;

import com.proyecto.gestioncitas.auth.domain.enums.EstadoUsuario;
import com.proyecto.gestioncitas.auth.domain.enums.Rol;
import java.time.Instant;

/**
 * Vista publica de un usuario. No incluye el hash de la contrasena ni el
 * contador de intentos fallidos.
 */
public record UsuarioResponse(
        Long id,
        String email,
        String dni,
        Rol rol,
        EstadoUsuario estado,
        Instant ultimoAcceso,
        Instant createdAt) {
}
