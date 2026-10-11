package com.proyecto.gestioncitas.config.security;

import com.proyecto.gestioncitas.auth.domain.entity.Usuario;
import com.proyecto.gestioncitas.auth.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@Primary
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    @Override
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
        Usuario u = usuarioRepository.findById(userId)
            .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + userId));
        return new org.springframework.security.core.userdetails.User(
            u.getId(), u.getPasswordHash(),
            List.of(new SimpleGrantedAuthority("ROLE_" + u.getRol().name())));
    }
}
