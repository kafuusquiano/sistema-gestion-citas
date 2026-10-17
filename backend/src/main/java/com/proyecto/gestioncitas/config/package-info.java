/**
 * Configuracion global: seguridad con JWT, cache, documentacion de la API,
 * serializacion y ejecucion en segundo plano.
 *
 * Tambien es un modulo ABIERTO, por la misma razon que common: es
 * infraestructura, no dominio. Los modulos usan sus clases (JwtTokenProvider,
 * TokenRevocationStore) y config no conoce a ninguno de ellos.
 */
@org.springframework.modulith.ApplicationModule(
        type = org.springframework.modulith.ApplicationModule.Type.OPEN)
package com.proyecto.gestioncitas.config;
