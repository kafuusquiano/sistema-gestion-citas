package com.proyecto.gestioncitas.config.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    @Override
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
        // TODO: implementación real en módulo auth
        throw new UsernameNotFoundException("Implementación pendiente en módulo auth");
    }
}
