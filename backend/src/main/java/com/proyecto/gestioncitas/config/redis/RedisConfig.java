package com.proyecto.gestioncitas.config.redis;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;

/**
 * Acceso directo a Redis. Lo usa TokenBlacklistService del modulo auth para
 * guardar los identificadores de los tokens revocados al cerrar sesion, con un
 * tiempo de vida igual a lo que al token le quedaba de vigencia.
 *
 * Es StringRedisTemplate y no RedisTemplate<String, Object>: los valores que
 * se guardan son marcas y jti, texto plano. Evita arrastrar un serializador
 * JSON a Redis y el acoplamiento con Jackson que eso implica.
 *
 * La condicion es la propiedad app.cache.redis y NO el perfil activo. Con
 * @Profile("prod") este bean se creaba siempre en produccion, y al pedir el
 * RedisConnectionFactory obligaba a instanciarlo aunque Redis no estuviera
 * configurado: el arranque fallaba con "'host' must not be empty" antes de
 * llegar a usar nada. Atandolo a la propiedad, sin Redis el bean no existe y
 * la fabrica de conexiones no llega a crearse.
 *
 * Ademas, conviene recordar que StringRedisTemplate lo autoconfigura Spring
 * Boot en cualquier perfil por el solo hecho de tener el starter en el
 * classpath. Nadie debe decidir "uso Redis si el bean existe".
 */
@Configuration
@ConditionalOnProperty(name = "app.cache.redis", havingValue = "true")
public class RedisConfig {

    @Bean
    public StringRedisTemplate stringRedisTemplate(RedisConnectionFactory conexion) {
        return new StringRedisTemplate(conexion);
    }
}
