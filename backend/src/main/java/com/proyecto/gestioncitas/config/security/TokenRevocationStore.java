package com.proyecto.gestioncitas.config.security;

/**
 * Puerto que el filtro consulta para saber si un token fue revocado.
 *
 * Existe para no romper la regla M1. JwtAuthenticationFilter necesita
 * preguntar por la lista negra, pero esa lista la administra el modulo auth,
 * y config no puede importar clases internas de un modulo de dominio.
 *
 * La direccion de la dependencia queda al reves: config declara el contrato y
 * auth lo implementa, igual que auth ya usa JwtTokenProvider de config.
 *
 * Se inyecta con ObjectProvider en el filtro, para que config siga arrancando
 * aunque ninguna implementacion este presente.
 */
public interface TokenRevocationStore {

    /**
     * @param jti identificador del token
     * @return true si el token fue revocado y debe rechazarse
     */
    boolean estaRevocado(String jti);
}
