package com.proyecto.gestioncitas.auth.service;

import com.proyecto.gestioncitas.auth.domain.dto.response.UsuarioResponse;
import com.proyecto.gestioncitas.common.dto.PageResponse;

public interface UsuarioService {
    UsuarioResponse obtenerPorId(String id);
    PageResponse<UsuarioResponse> listarTodos(int page, int size);
    void desactivar(String id);
}
