package com.proyecto.gestioncitas.medico.mapper;

import com.proyecto.gestioncitas.medico.domain.dto.response.EspecialidadResponse;
import com.proyecto.gestioncitas.medico.domain.dto.response.MedicoResponse;
import com.proyecto.gestioncitas.medico.domain.dto.response.MedicoResumenResponse;
import com.proyecto.gestioncitas.medico.domain.entity.Especialidad;
import com.proyecto.gestioncitas.medico.domain.entity.Medico;
import com.proyecto.gestioncitas.medico.domain.entity.MedicoEspecialista;
import com.proyecto.gestioncitas.medico.domain.entity.MedicoGeneral;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

/**
 * Entidad a DTO. Usa HorarioMapper para las franjas.
 *
 * Los campos tipo, descripcionProfesional, duracionConsultaMinutos y
 * recibeDerivaciones salen de los metodos abstractos de Medico: la respuesta
 * JSON es distinta segun la subclase sin que el mapper pregunte de que tipo
 * se trata. Es el polimorfismo de la seccion 1.3.1 atravesando hasta la API.
 *
 * Los campos exclusivos de cada subclase si requieren instanceof, porque un
 * record plano no puede heredar: la alternativa seria dos DTOs distintos, y
 * eso obligaria al frontend a manejar dos formas de respuesta.
 */
@Mapper(componentModel = "spring", uses = HorarioMapper.class)
public interface MedicoMapper {

    @Mapping(target = "tipo", expression = "java(medico.tipo())")
    @Mapping(target = "nombreCompleto", expression = "java(medico.nombreCompleto())")
    @Mapping(target = "descripcionProfesional", expression = "java(medico.descripcionProfesional())")
    @Mapping(target = "firmaProfesional", expression = "java(medico.firmaProfesional())")
    @Mapping(target = "duracionConsultaMinutos", expression = "java(medico.duracionConsultaMinutos())")
    @Mapping(target = "recibeDerivaciones", expression = "java(medico.recibeDerivaciones())")
    @Mapping(target = "rne", source = "medico", qualifiedByName = "rneDe")
    @Mapping(target = "aniosExperiencia", source = "medico", qualifiedByName = "experienciaDe")
    @Mapping(target = "areaRotacion", source = "medico", qualifiedByName = "areaRotacionDe")
    MedicoResponse aResponse(Medico medico);

    @Mapping(target = "tipo", expression = "java(medico.tipo())")
    @Mapping(target = "nombreCompleto", expression = "java(medico.nombreCompleto())")
    @Mapping(target = "especialidad", expression = "java(medico.getEspecialidad().getNombre())")
    @Mapping(target = "descripcionProfesional", expression = "java(medico.descripcionProfesional())")
    @Mapping(target = "duracionConsultaMinutos", expression = "java(medico.duracionConsultaMinutos())")
    @Mapping(target = "admiteCitas", expression = "java(medico.admiteCitas())")
    MedicoResumenResponse aResumen(Medico medico);

    EspecialidadResponse aEspecialidadResponse(Especialidad especialidad);

    List<EspecialidadResponse> aEspecialidadResponses(List<Especialidad> especialidades);

    @Named("rneDe")
    default String rneDe(Medico medico) {
        return medico instanceof MedicoEspecialista esp ? esp.getRne() : null;
    }

    @Named("experienciaDe")
    default Short experienciaDe(Medico medico) {
        return medico instanceof MedicoEspecialista esp ? esp.getAniosExperiencia() : null;
    }

    @Named("areaRotacionDe")
    default String areaRotacionDe(Medico medico) {
        return medico instanceof MedicoGeneral gen ? gen.getAreaRotacion() : null;
    }
}
