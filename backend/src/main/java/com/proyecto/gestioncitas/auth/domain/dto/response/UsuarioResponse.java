package com.proyecto.gestioncitas.auth.domain.dto.response;

import com.proyecto.gestioncitas.auth.domain.enums.EstadoUsuario;
import com.proyecto.gestioncitas.auth.domain.enums.Rol;
import lombok.Builder;
import lombok.Getter;
import java.time.Instant;

@Getter
@Builder
public class UsuarioResponse {
    private final String id;
    private final String nombre;
    private final String apellido;
    private final String email;
    private final String dni;
    private final Rol rol;
    private final EstadoUsuario estado;
    private final Instant createdAt;
}
