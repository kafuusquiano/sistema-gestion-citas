package com.proyecto.gestioncitas.paciente.mapper;

import com.proyecto.gestioncitas.paciente.domain.dto.response.PacienteResponse;
import com.proyecto.gestioncitas.paciente.domain.dto.response.PacienteResumenResponse;
import com.proyecto.gestioncitas.paciente.domain.entity.ContactoEmergencia;
import com.proyecto.gestioncitas.paciente.domain.entity.Paciente;
import com.proyecto.gestioncitas.paciente.domain.enums.GrupoSanguineo;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

/**
 * Entidad a DTO con MapStruct.
 *
 * Los campos calculados (nombreCompleto, edad, tieneCuenta) se toman de los
 * metodos de la entidad: la regla de negocio vive en Paciente y el mapper
 * solo la invoca, en lugar de duplicar el calculo aqui.
 *
 * El grupo sanguineo sale como etiqueta ("O+") y no como nombre de constante,
 * que es lo que el frontend muestra tal cual.
 */
@Mapper(componentModel = "spring")
public interface PacienteMapper {

    @Mapping(target = "nombreCompleto", expression = "java(paciente.nombreCompleto())")
    @Mapping(target = "edad", expression = "java(paciente.edad())")
    @Mapping(target = "tieneCuenta", expression = "java(paciente.tieneCuenta())")
    @Mapping(target = "grupoSanguineo", source = "grupoSanguineo", qualifiedByName = "grupoAEtiqueta")
    PacienteResponse aResponse(Paciente paciente);

    @Mapping(target = "nombreCompleto", expression = "java(paciente.nombreCompleto())")
    @Mapping(target = "edad", expression = "java(paciente.edad())")
    @Mapping(target = "activo", expression = "java(paciente.puedeAgendar())")
    PacienteResumenResponse aResumen(Paciente paciente);

    List<PacienteResumenResponse> aResumenes(List<Paciente> pacientes);

    PacienteResponse.ContactoEmergenciaResponse aContactoResponse(ContactoEmergencia contacto);

    @Named("grupoAEtiqueta")
    default String grupoAEtiqueta(GrupoSanguineo grupo) {
        return grupo == null ? null : grupo.getEtiqueta();
    }
}
