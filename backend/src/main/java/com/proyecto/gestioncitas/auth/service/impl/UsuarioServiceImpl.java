package com.proyecto.gestioncitas.auth.service.impl;

import com.proyecto.gestioncitas.auth.domain.dto.response.UsuarioResponse;
import com.proyecto.gestioncitas.auth.mapper.UsuarioMapper;
import com.proyecto.gestioncitas.auth.repository.UsuarioRepository;
import com.proyecto.gestioncitas.auth.service.UsuarioService;
import com.proyecto.gestioncitas.common.dto.PageResponse;
import com.proyecto.gestioncitas.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final UsuarioMapper     usuarioMapper;

    @Override
    public UsuarioResponse obtenerPorId(String id) {
        return usuarioMapper.toResponse(
            usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", "id", id)));
    }

    @Override
    public PageResponse<UsuarioResponse> listarTodos(int page, int size) {
        Page<UsuarioResponse> p = usuarioRepository
            .findAll(PageRequest.of(page, size))
            .map(usuarioMapper::toResponse);
        return PageResponse.<UsuarioResponse>builder()
            .content(p.getContent()).page(p.getNumber())
            .size(p.getSize()).totalElements(p.getTotalElements())
            .totalPages(p.getTotalPages()).last(p.isLast()).build();
    }

    @Override
    @Transactional
    public void desactivar(String id) {
        usuarioRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Usuario", "id", id));
        // TODO: cambiar estado a INACTIVO
    }
}
