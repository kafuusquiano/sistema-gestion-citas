package com.proyecto.gestioncitas.paciente.mapper;

import com.proyecto.gestioncitas.paciente.domain.dto.response.PacienteResponse;
import com.proyecto.gestioncitas.paciente.domain.entity.Paciente;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PacienteMapper {
    PacienteResponse toResponse(Paciente paciente);
}
