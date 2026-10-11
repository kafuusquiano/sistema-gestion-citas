package com.proyecto.gestioncitas.auth.mapper;

import com.proyecto.gestioncitas.auth.domain.dto.response.UsuarioResponse;
import com.proyecto.gestioncitas.auth.domain.entity.Usuario;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-10-11T18:48:15-0500",
    comments = "version: 1.5.5.Final, compiler: Eclipse JDT (IDE) 3.46.100.v20260826-1225, environment: Java 21.0.12.1 (Eclipse Adoptium)"
)
@Component
public class UsuarioMapperImpl implements UsuarioMapper {

    @Override
    public UsuarioResponse toResponse(Usuario usuario) {
        if ( usuario == null ) {
            return null;
        }

        UsuarioResponse.UsuarioResponseBuilder usuarioResponse = UsuarioResponse.builder();

        usuarioResponse.id( usuario.getId() );
        usuarioResponse.nombre( usuario.getNombre() );
        usuarioResponse.apellido( usuario.getApellido() );
        usuarioResponse.email( usuario.getEmail() );
        usuarioResponse.dni( usuario.getDni() );
        usuarioResponse.rol( usuario.getRol() );
        usuarioResponse.estado( usuario.getEstado() );
        usuarioResponse.createdAt( usuario.getCreatedAt() );

        return usuarioResponse.build();
    }
}
