package com.proyecto.gestioncitas.medico.mapper;

import com.proyecto.gestioncitas.medico.domain.dto.response.*;
import com.proyecto.gestioncitas.medico.domain.entity.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface MedicoMapper {
    @Mapping(source = "especialidad.id",     target = "especialidadId")
    @Mapping(source = "especialidad.nombre", target = "especialidadNombre")
    MedicoResponse toResponse(Medico medico);

    @Mapping(source = "medico.id", target = "medicoId")
    HorarioResponse toHorarioResponse(HorarioMedico horario);

    EspecialidadResponse toEspecialidadResponse(Especialidad especialidad);
}
