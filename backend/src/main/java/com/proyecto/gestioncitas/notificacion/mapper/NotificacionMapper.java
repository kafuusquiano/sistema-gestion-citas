package com.proyecto.gestioncitas.notificacion.mapper;

import com.proyecto.gestioncitas.notificacion.domain.dto.response.NotificacionResponse;
import com.proyecto.gestioncitas.notificacion.domain.entity.Notificacion;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Entidad a DTO. No expone intentos ni ultimoError: son datos de operacion del
 * sistema, no informacion para el paciente.
 */
@Mapper(componentModel = "spring")
public interface NotificacionMapper {

    @Mapping(target = "leida", expression = "java(notificacion.estaLeida())")
    NotificacionResponse aResponse(Notificacion notificacion);
}
