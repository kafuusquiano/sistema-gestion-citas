package com.proyecto.gestioncitas.auth.service.impl;

import com.proyecto.gestioncitas.auth.domain.dto.response.UsuarioResponse;
import com.proyecto.gestioncitas.auth.domain.entity.Usuario;
import com.proyecto.gestioncitas.auth.domain.enums.EstadoUsuario;
import com.proyecto.gestioncitas.auth.domain.enums.Rol;
import com.proyecto.gestioncitas.auth.exception.EmailYaRegistradaException;
import com.proyecto.gestioncitas.auth.mapper.UsuarioMapper;
import com.proyecto.gestioncitas.auth.repository.RefreshTokenRepository;
import com.proyecto.gestioncitas.auth.repository.UsuarioRepository;
import com.proyecto.gestioncitas.auth.service.UsuarioService;
import com.proyecto.gestioncitas.common.dto.PageResponse;
import com.proyecto.gestioncitas.common.exception.ErrorCode;
import com.proyecto.gestioncitas.common.exception.ResourceNotFoundException;
import java.time.Instant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class UsuarioServiceImpl implements UsuarioService {

    private static final Logger log = LoggerFactory.getLogger(UsuarioServiceImpl.class);

    private final UsuarioRepository usuarioRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UsuarioMapper mapper;
    private final PasswordEncoder passwordEncoder;

    public UsuarioServiceImpl(UsuarioRepository usuarioRepository,
            RefreshTokenRepository refreshTokenRepository,
            UsuarioMapper mapper,
            PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.mapper = mapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public PageResponse<UsuarioResponse> buscar(Rol rol, EstadoUsuario estado, String texto,
            Pageable pageable) {
        String filtro = (texto == null || texto.isBlank()) ? null : texto.trim();
        return PageResponse.de(usuarioRepository.buscar(rol, estado, filtro, pageable),
                mapper::aResponse);
    }

    @Override
    public UsuarioResponse obtener(Long id) {
        return mapper.aResponse(obtenerEntidad(id));
    }

    @Override
    public Usuario obtenerEntidad(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        ErrorCode.RECURSO_NO_ENCONTRADO, "Usuario", id));
    }

    @Override
    @Transactional
    public UsuarioResponse cambiarEstado(Long id, EstadoUsuario nuevoEstado) {
        Usuario usuario = obtenerEntidad(id);
        usuario.cambiarEstado(nuevoEstado);

        // Una cuenta inhabilitada no debe poder seguir renovando su token.
        // Sin esto, el usuario bloqueado conservaria acceso hasta que venciera
        // su refresh token, que son siete dias.
        if (!nuevoEstado.puedeIniciarSesion()) {
            int cerradas = refreshTokenRepository.revocarTodosDelUsuario(id, Instant.now());
            log.info("Cuenta {} paso a {}: se cerraron {} sesiones", id, nuevoEstado, cerradas);
        }

        return mapper.aResponse(usuario);
    }

    @Override
    @Transactional
    public Usuario crearCuenta(String email, String dni, String passwordEnClaro, Rol rol) {
        String correo = email.trim().toLowerCase();

        if (usuarioRepository.existsByEmail(correo)) {
            throw new EmailYaRegistradaException();
        }
        if (usuarioRepository.existsByDni(dni)) {
            throw EmailYaRegistradaException.porDniDuplicado();
        }

        Usuario usuario = new Usuario(correo, dni, passwordEncoder.encode(passwordEnClaro), rol);
        Usuario guardado = usuarioRepository.save(usuario);

        log.info("Cuenta creada: id {} con rol {}", guardado.getId(), rol);
        return guardado;
    }
}
