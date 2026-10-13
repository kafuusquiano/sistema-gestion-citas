package com.proyecto.gestioncitas.common.util;

import java.util.Optional;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Acceso al usuario autenticado desde el codigo de negocio.
 *
 * El JwtAuthenticationFilter deja el id del usuario como nombre de la
 * Authentication y el rol como authority, asi que estos metodos no consultan
 * la base de datos.
 *
 * Se usa sobre todo para las reglas de "solo sus propios datos": un paciente
 * que pide /api/citas/mis-citas o /api/pacientes/me.
 */
public final class SecurityUtils {

    private SecurityUtils() {
    }

    public static final String USUARIO_SISTEMA = "sistema";

    private static final String PREFIJO_ROL = "ROLE_";

    private static Optional<Authentication> autenticacion() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return Optional.empty();
        }
        // El usuario anonimo cuenta como no autenticado para el negocio.
        if ("anonymousUser".equals(auth.getPrincipal())) {
            return Optional.empty();
        }
        return Optional.of(auth);
    }

    /** Id del usuario autenticado, vacio si la peticion es publica. */
    public static Optional<Long> obtenerUsuarioActual() {
        return autenticacion()
                .map(Authentication::getName)
                .flatMap(nombre -> {
                    try {
                        return Optional.of(Long.valueOf(nombre));
                    } catch (NumberFormatException e) {
                        return Optional.empty();
                    }
                });
    }

    /**
     * Id del usuario como texto, o "sistema" cuando la accion la ejecuta un
     * proceso automatico como un scheduler. Lo usa AuditableEntity.
     */
    public static String obtenerUsuarioActualODefecto() {
        return autenticacion().map(Authentication::getName).orElse(USUARIO_SISTEMA);
    }

    /** Rol sin el prefijo ROLE_. Ejemplo: "MEDICO". */
    public static Optional<String> obtenerRolActual() {
        return autenticacion()
                .flatMap(auth -> auth.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .filter(a -> a.startsWith(PREFIJO_ROL))
                        .map(a -> a.substring(PREFIJO_ROL.length()))
                        .findFirst());
    }

    /** Recibe el rol sin prefijo. Ejemplo: tieneRol("ADMIN"). */
    public static boolean tieneRol(String rol) {
        return autenticacion()
                .map(auth -> auth.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .anyMatch(a -> a.equals(PREFIJO_ROL + rol)))
                .orElse(false);
    }

    public static boolean estaAutenticado() {
        return autenticacion().isPresent();
    }

    /** true si el id recibido es el del usuario autenticado. */
    public static boolean esElMismoUsuario(Long usuarioId) {
        return usuarioId != null && obtenerUsuarioActual().map(usuarioId::equals).orElse(false);
    }
}
