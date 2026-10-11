package com.proyecto.gestioncitas.auth.api;

import com.proyecto.gestioncitas.auth.domain.dto.response.UsuarioResponse;
import java.util.Optional;

public interface AuthFacade {
    Optional<UsuarioResponse> obtenerUsuarioPorId(String id);
    boolean existeUsuario(String id);
}
