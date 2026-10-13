package com.proyecto.gestioncitas.auth.service;

import com.proyecto.gestioncitas.config.security.TokenRevocationStore;

/**
 * Lista negra de access tokens revocados.
 *
 * Hace falta porque un JWT es valido hasta que caduca: al cerrar sesion, el
 * token que el cliente ya tiene seguiria funcionando durante lo que le quede
 * de vigencia. Esta lista permite rechazarlo antes.
 *
 * Extiende TokenRevocationStore, que es el puerto que consulta
 * JwtAuthenticationFilter. Asi el filtro lee la lista negra sin importar nada
 * del modulo auth, y el metodo estaRevocado() se hereda de ese contrato.
 */
public interface TokenBlacklistService extends TokenRevocationStore {

    /**
     * Marca un token como revocado.
     *
     * @param jti identificador del token
     * @param milisegundosRestantes lo que le queda de vigencia; pasado ese
     *        tiempo la marca se descarta sola, porque el token ya no sirve
     *        para nada y guardarlo mas seria desperdiciar memoria
     */
    void revocar(String jti, long milisegundosRestantes);
}
