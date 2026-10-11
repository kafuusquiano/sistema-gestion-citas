package com.proyecto.gestioncitas.auth.mapper;

import com.proyecto.gestioncitas.auth.domain.dto.response.UsuarioResponse;
import com.proyecto.gestioncitas.auth.domain.entity.Usuario;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UsuarioMapper {
    UsuarioResponse toResponse(Usuario usuario);
}
