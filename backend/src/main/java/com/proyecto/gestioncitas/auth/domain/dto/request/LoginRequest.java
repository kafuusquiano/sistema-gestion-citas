package com.proyecto.gestioncitas.auth.domain.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * Cuerpo de POST /api/auth/login.
 */
public record LoginRequest(

        @NotBlank(message = "El correo es obligatorio")
        @Email(message = "El correo no tiene un formato valido")
        String email,

        @NotBlank(message = "La contrasena es obligatoria")
        String password) {
}
