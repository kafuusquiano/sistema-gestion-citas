package com.proyecto.gestioncitas.auth.api;

import com.proyecto.gestioncitas.auth.domain.entity.Usuario;
import com.proyecto.gestioncitas.auth.repository.UsuarioRepository;
import com.proyecto.gestioncitas.auth.service.UsuarioService;
import com.proyecto.gestioncitas.common.exception.ErrorCode;
import com.proyecto.gestioncitas.common.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementacion de la fachada. Traduce entre la entidad interna y los
 * records publicos del contrato, de modo que la entidad Usuario nunca sale
 * del modulo.
 */
@Service
@Transactional(readOnly = true)
public class AuthFacadeImpl implements AuthFacade {

    private final UsuarioRepository usuarioRepository;
    private final UsuarioService usuarioService;

    public AuthFacadeImpl(UsuarioRepository usuarioRepository, UsuarioService usuarioService) {
        this.usuarioRepository = usuarioRepository;
        this.usuarioService = usuarioService;
    }

    @Override
    public boolean existe(Long usuarioId) {
        return usuarioId != null && usuarioRepository.existsById(usuarioId);
    }

    @Override
    public UsuarioPublico obtener(Long usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        ErrorCode.RECURSO_NO_ENCONTRADO, "Usuario", usuarioId));

        return new UsuarioPublico(
                usuario.getId(),
                usuario.getEmail(),
                usuario.getDni(),
                usuario.getRol(),
                usuario.puedeIniciarSesion());
    }

    @Override
    @Transactional
    public Long crearCuenta(NuevaCuenta datos) {
        return usuarioService.crearCuenta(
                datos.email(), datos.dni(), datos.passwordEnClaro(), datos.rol()).getId();
    }
}
