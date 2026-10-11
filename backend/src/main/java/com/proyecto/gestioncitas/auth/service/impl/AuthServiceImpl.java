package com.proyecto.gestioncitas.auth.service.impl;

import com.proyecto.gestioncitas.auth.domain.dto.request.*;
import com.proyecto.gestioncitas.auth.domain.dto.response.AuthResponse;
import com.proyecto.gestioncitas.auth.domain.entity.*;
import com.proyecto.gestioncitas.auth.domain.enums.Rol;
import com.proyecto.gestioncitas.auth.event.UsuarioRegistradoEvent;
import com.proyecto.gestioncitas.auth.repository.*;
import com.proyecto.gestioncitas.auth.service.AuthService;
import com.proyecto.gestioncitas.common.event.EventPublisher;
import com.proyecto.gestioncitas.common.exception.*;
import com.proyecto.gestioncitas.config.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.Instant;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UsuarioRepository       usuarioRepository;
    private final RefreshTokenRepository  refreshTokenRepository;
    private final PasswordEncoder         passwordEncoder;
    private final JwtTokenProvider        tokenProvider;
    private final EventPublisher          eventPublisher;

    @Override
    @Transactional
    public AuthResponse registrar(RegisterRequest req) {
        if (usuarioRepository.existsByEmail(req.getEmail()))
            throw new BusinessException(ErrorCode.EMAIL_YA_REGISTRADO,
                "El email ya está registrado: " + req.getEmail());

        Usuario usuario = Usuario.builder()
            .nombre(req.getNombre())
            .apellido(req.getApellido())
            .email(req.getEmail())
            .dni(req.getDni())
            .passwordHash(passwordEncoder.encode(req.getPassword()))
            .rol(Rol.valueOf(req.getRol()))
            .build();

        usuarioRepository.save(usuario);
        eventPublisher.publish(new UsuarioRegistradoEvent(
            usuario.getId(), usuario.getEmail(), usuario.getNombre(), usuario.getRol().name()));

        return generarTokens(usuario);
    }

    @Override
    @Transactional
    public AuthResponse login(LoginRequest req) {
        Usuario usuario = usuarioRepository.findByEmail(req.getEmail())
            .orElseThrow(() -> new BusinessException(
                ErrorCode.CREDENCIALES_INVALIDAS, "Credenciales inválidas"));

        if (!passwordEncoder.matches(req.getPassword(), usuario.getPasswordHash()))
            throw new BusinessException(ErrorCode.CREDENCIALES_INVALIDAS, "Credenciales inválidas");

        return generarTokens(usuario);
    }

    @Override
    @Transactional
    public AuthResponse refrescarToken(RefreshTokenRequest req) {
        RefreshToken rt = refreshTokenRepository.findByToken(req.getRefreshToken())
            .filter(t -> !t.isRevocado() && t.getExpiraEn().isAfter(Instant.now()))
            .orElseThrow(() -> new BusinessException(ErrorCode.TOKEN_INVALIDO, "Refresh token inválido o expirado"));

        Usuario usuario = usuarioRepository.findById(rt.getUsuarioId())
            .orElseThrow(() -> new ResourceNotFoundException("Usuario", "id", rt.getUsuarioId()));

        rt.setRevocado(true);
        refreshTokenRepository.save(rt);
        return generarTokens(usuario);
    }

    @Override
    @Transactional
    public void logout(String usuarioId) {
        refreshTokenRepository.revocarTodosPorUsuario(usuarioId);
        log.info("Logout usuario: {}", usuarioId);
    }

    private AuthResponse generarTokens(Usuario usuario) {
        String access  = tokenProvider.generarAccessToken(usuario.getId(), usuario.getRol().name());
        String refresh = tokenProvider.generarRefreshToken(usuario.getId());

        refreshTokenRepository.save(RefreshToken.builder()
            .token(refresh)
            .usuarioId(usuario.getId())
            .expiraEn(Instant.now().plusMillis(604800000L))
            .build());

        return AuthResponse.builder()
            .accessToken(access)
            .refreshToken(refresh)
            .tipo("Bearer")
            .userId(usuario.getId())
            .email(usuario.getEmail())
            .rol(usuario.getRol().name())
            .build();
    }
}
