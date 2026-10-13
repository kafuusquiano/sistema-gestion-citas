package com.proyecto.gestioncitas.config.redis;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;

/**
 * Acceso directo a Redis, solo en produccion.
 *
 * Lo usa TokenBlacklistService del modulo auth para guardar los jti revocados
 * al cerrar sesion, con un TTL igual a lo que le quedaba de vigencia al token.
 *
 * Es StringRedisTemplate y no RedisTemplate<String, Object>: los valores que
 * se guardan son marcas y jti, texto plano. Evita arrastrar un serializador
 * JSON a Redis y el acoplamiento con Jackson que eso implica.
 *
 * En dev no existe este bean. El modulo auth debe tratar la lista negra como
 * opcional, con un ObjectProvider o un Optional, y no inyectarla directo.
 */
@Configuration
@Profile("prod")
public class RedisConfig {

    @Bean
    public StringRedisTemplate stringRedisTemplate(RedisConnectionFactory conexion) {
        return new StringRedisTemplate(conexion);
    }
}
