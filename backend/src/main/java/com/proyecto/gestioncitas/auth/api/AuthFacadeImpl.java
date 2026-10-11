package com.proyecto.gestioncitas.auth.api;

import com.proyecto.gestioncitas.auth.domain.dto.response.UsuarioResponse;
import com.proyecto.gestioncitas.auth.repository.UsuarioRepository;
import com.proyecto.gestioncitas.auth.mapper.UsuarioMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class AuthFacadeImpl implements AuthFacade {

    private final UsuarioRepository usuarioRepository;
    private final UsuarioMapper     usuarioMapper;

    @Override
    public Optional<UsuarioResponse> obtenerUsuarioPorId(String id) {
        return usuarioRepository.findById(id).map(usuarioMapper::toResponse);
    }

    @Override
    public boolean existeUsuario(String id) {
        return usuarioRepository.existsById(id);
    }
}
