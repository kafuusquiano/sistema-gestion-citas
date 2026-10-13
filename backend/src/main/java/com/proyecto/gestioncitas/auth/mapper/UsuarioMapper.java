package com.proyecto.gestioncitas.auth.mapper;

import com.proyecto.gestioncitas.auth.domain.entity.Usuario;
import com.proyecto.gestioncitas.auth.domain.dto.response.UsuarioResponse;
import org.mapstruct.Mapper;

/**
 * Entidad a DTO con MapStruct.
 *
 * No hay metodo inverso de DTO a entidad: Usuario se construye solo por su
 * constructor, que exige el hash ya calculado. Un mapper que armara la
 * entidad podria dejar passwordHash en nulo o con la contrasena en claro.
 */
@Mapper(componentModel = "spring")
public interface UsuarioMapper {

    UsuarioResponse aResponse(Usuario usuario);
}
