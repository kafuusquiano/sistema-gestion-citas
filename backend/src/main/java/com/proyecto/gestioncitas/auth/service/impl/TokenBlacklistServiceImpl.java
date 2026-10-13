package com.proyecto.gestioncitas.auth.service.impl;

import com.proyecto.gestioncitas.auth.service.TokenBlacklistService;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

/**
 * Lista negra de tokens revocados. Redis en produccion, memoria en desarrollo.
 *
 * El respaldo en memoria basta para desarrollo, pero tiene dos limites que en
 * produccion importan: se pierde al reiniciar el proceso y no se comparte
 * entre instancias. De ahi que prod use Redis.
 *
 * CUIDADO con como se elige una u otra. No sirve preguntar si el bean
 * StringRedisTemplate existe: Spring Boot lo autoconfigura en TODOS los
 * perfiles por el solo hecho de tener spring-boot-starter-data-redis en el
 * classpath. Si se decidiera por presencia del bean, en dev se encontraria la
 * plantilla, intentaria conectar a localhost:6379 y fallaria en cada peticion
 * autenticada. La decision va por la propiedad app.seguridad.lista-negra-redis,
 * que es false por defecto y true solo en el perfil prod.
 */
@Service
public class TokenBlacklistServiceImpl implements TokenBlacklistService {

    private static final Logger log = LoggerFactory.getLogger(TokenBlacklistServiceImpl.class);

    private static final String PREFIJO = "jwt:revocado:";

    private final ObjectProvider<StringRedisTemplate> redis;
    private final boolean usarRedis;

    /** Respaldo para dev: jti como clave, instante de caducidad como valor. */
    private final Map<String, Long> enMemoria = new ConcurrentHashMap<>();

    public TokenBlacklistServiceImpl(ObjectProvider<StringRedisTemplate> redis,
            @Value("${app.seguridad.lista-negra-redis:false}") boolean usarRedis) {
        this.redis = redis;
        this.usarRedis = usarRedis;
        log.info("Lista negra de tokens: {}", usarRedis ? "Redis" : "memoria local");
    }

    @Override
    public void revocar(String jti, long milisegundosRestantes) {
        if (jti == null || jti.isBlank() || milisegundosRestantes <= 0) {
            return;
        }

        StringRedisTemplate plantilla = plantillaRedis();
        if (plantilla != null) {
            plantilla.opsForValue().set(PREFIJO + jti, "1",
                    Duration.ofMillis(milisegundosRestantes));
            log.debug("Token {} revocado en Redis", jti);
            return;
        }

        enMemoria.put(jti, System.currentTimeMillis() + milisegundosRestantes);
        limpiarVencidos();
        log.debug("Token {} revocado en memoria", jti);
    }

    @Override
    public boolean estaRevocado(String jti) {
        if (jti == null || jti.isBlank()) {
            return false;
        }

        StringRedisTemplate plantilla = plantillaRedis();
        if (plantilla != null) {
            return Boolean.TRUE.equals(plantilla.hasKey(PREFIJO + jti));
        }

        Long caduca = enMemoria.get(jti);
        if (caduca == null) {
            return false;
        }
        if (caduca < System.currentTimeMillis()) {
            enMemoria.remove(jti);
            return false;
        }
        return true;
    }

    private StringRedisTemplate plantillaRedis() {
        return usarRedis ? redis.getIfAvailable() : null;
    }

    /**
     * Redis descarta las claves vencidas por su cuenta; el mapa en memoria no,
     * asi que hay que barrerlo o crece sin limite durante la jornada.
     */
    private void limpiarVencidos() {
        long ahora = System.currentTimeMillis();
        enMemoria.entrySet().removeIf(e -> e.getValue() < ahora);
    }
}
