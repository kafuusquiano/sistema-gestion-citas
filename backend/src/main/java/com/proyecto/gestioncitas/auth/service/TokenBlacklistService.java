package com.proyecto.gestioncitas.auth.service;

public interface TokenBlacklistService {
    void revocar(String token);
    boolean estaRevocado(String token);
}
