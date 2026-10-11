package com.proyecto.gestioncitas.auth.service.impl;

import com.proyecto.gestioncitas.auth.service.TokenBlacklistService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class TokenBlacklistServiceImpl implements TokenBlacklistService {

    private final RedisTemplate<String, Object> redisTemplate;
    private static final String PREFIX = "blacklist:token:";

    @Override
    public void revocar(String token) {
        redisTemplate.opsForValue().set(PREFIX + token, "revocado", 24, TimeUnit.HOURS);
    }

    @Override
    public boolean estaRevocado(String token) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(PREFIX + token));
    }
}
