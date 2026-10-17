package com.proyecto.gestioncitas.medico.mapper;

import com.proyecto.gestioncitas.medico.domain.dto.response.ExcepcionHorarioResponse;
import com.proyecto.gestioncitas.medico.domain.dto.response.HorarioResponse;
import com.proyecto.gestioncitas.medico.domain.entity.ExcepcionHorario;
import com.proyecto.gestioncitas.medico.domain.entity.Horario;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

/**
 * Franjas y excepciones a DTO.
 *
 * cantidadSlots sale del metodo de la entidad: el calculo de cuantos turnos
 * caben en una franja es regla de negocio y vive en Horario, no aqui.
 */
@Mapper(componentModel = "spring")
public interface HorarioMapper {

    @Mapping(target = "cantidadSlots", expression = "java(horario.cantidadSlots())")
    HorarioResponse aResponse(Horario horario);

    List<HorarioResponse> aResponses(List<Horario> horarios);

    @Mapping(target = "medicoId", source = "excepcion", qualifiedByName = "medicoIdDe")
    @Mapping(target = "general", expression = "java(excepcion.esGeneral())")
    ExcepcionHorarioResponse aExcepcionResponse(ExcepcionHorario excepcion);

    List<ExcepcionHorarioResponse> aExcepcionResponses(List<ExcepcionHorario> excepciones);

    /** Nulo cuando la excepcion aplica a todo el establecimiento. */
    @Named("medicoIdDe")
    default Long medicoIdDe(ExcepcionHorario excepcion) {
        return excepcion.getMedico() == null ? null : excepcion.getMedico().getId();
    }
}
