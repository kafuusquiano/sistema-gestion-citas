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
 * OJO: el @Profile("prod") de esta clase NO hace que el bean falte en dev.
 * Spring Boot autoconfigura StringRedisTemplate en todos los perfiles por el
 * solo hecho de tener spring-boot-starter-data-redis en el classpath; lo que
 * hace esta clase es sustituir esa definicion en prod. Por eso nadie debe
 * decidir "uso Redis si el bean existe": en dev existe igual y al usarlo
 * fallaria al conectar. Quien lo necesite se guia por la propiedad
 * app.seguridad.lista-negra-redis.
 */
@Configuration
@Profile("prod")
public class RedisConfig {

    @Bean
    public StringRedisTemplate stringRedisTemplate(RedisConnectionFactory conexion) {
        return new StringRedisTemplate(conexion);
    }
}
