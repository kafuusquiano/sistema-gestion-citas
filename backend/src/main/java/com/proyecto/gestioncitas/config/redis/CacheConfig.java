package com.proyecto.gestioncitas.config.redis;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.proyecto.gestioncitas.common.constants.AppConstants;
import java.time.Duration;
import java.util.Map;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * Cache con TTL distinto por nombre (instruccion M4).
 *
 * Esto no se puede expresar con spring.cache.caffeine.spec ni con
 * spring.cache.redis.time-to-live, porque ambas propiedades aplican un unico
 * TTL a todas las caches. De ahi que haga falta declarar los CacheManager.
 *
 * Las dos implementaciones respetan los mismos nombres y los mismos tiempos,
 * asi que el codigo de negocio usa @Cacheable y @CacheEvict sin saber cual
 * esta activa.
 *
 * La eleccion la hace la propiedad app.cache.redis y NO el perfil activo. Con
 * @Profile, produccion quedaria atada a tener Redis: si no esta configurado,
 * cada @Cacheable fallaria al intentar conectar. Con la propiedad, el servicio
 * arranca con Caffeine y se pasa a Redis poniendo REDIS_ENABLED=true.
 */
@Configuration
public class CacheConfig {

    /** Slots libres de un medico. Corto: cambia con cada cita que se mueve. */
    private static final Duration TTL_SLOTS = Duration.ofMinutes(5);

    /** Horario semanal del medico. Cambia poco, lo invalida su evento. */
    private static final Duration TTL_HORARIOS = Duration.ofMinutes(30);

    /** Catalogo de especialidades, practicamente estatico. */
    private static final Duration TTL_ESPECIALIDADES = Duration.ofHours(6);

    private static final Map<String, Duration> TTL_POR_CACHE = Map.of(
            AppConstants.CACHE_SLOTS, TTL_SLOTS,
            AppConstants.CACHE_HORARIOS, TTL_HORARIOS,
            AppConstants.CACHE_ESPECIALIDADES, TTL_ESPECIALIDADES);

    @Bean
    @ConditionalOnProperty(name = "app.cache.redis", havingValue = "false", matchIfMissing = true)
    public CacheManager caffeineCacheManager() {
        CaffeineCacheManager gestor = new CaffeineCacheManager();
        // Sin esto, una cache con nombre no registrado se crearia sin TTL.
        gestor.setAllowNullValues(false);

        TTL_POR_CACHE.forEach((nombre, ttl) -> gestor.registerCustomCache(nombre,
                Caffeine.newBuilder()
                        .expireAfterWrite(ttl)
                        .maximumSize(1_000)
                        // Sin recordStats() Micrometer avisa que no puede medir
                        // la cache y solo expone cache.size.
                        .recordStats()
                        .build()));

        return gestor;
    }

    @Bean
    @ConditionalOnProperty(name = "app.cache.redis", havingValue = "true")
    public CacheManager redisCacheManager(
            org.springframework.data.redis.connection.RedisConnectionFactory conexion) {

        RedisCacheConfiguration base = RedisCacheConfiguration.defaultCacheConfig()
                .disableCachingNullValues()
                .serializeKeysWith(RedisSerializationContext.SerializationPair
                        .fromSerializer(new StringRedisSerializer()))
                // Prefijo propio: Upstash free es una sola base compartida por la app.
                .prefixCacheNameWith("gestion-citas::");

        var configuraciones = TTL_POR_CACHE.entrySet().stream()
                .collect(java.util.stream.Collectors.toMap(
                        Map.Entry::getKey,
                        e -> base.entryTtl(e.getValue())));

        return RedisCacheManager.builder(conexion)
                .cacheDefaults(base.entryTtl(Duration.ofMinutes(10)))
                .withInitialCacheConfigurations(configuraciones)
                .build();
    }
}
