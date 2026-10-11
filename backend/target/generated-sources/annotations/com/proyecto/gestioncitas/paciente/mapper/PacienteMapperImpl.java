package com.proyecto.gestioncitas.paciente.mapper;

import com.proyecto.gestioncitas.paciente.domain.dto.response.PacienteResponse;
import com.proyecto.gestioncitas.paciente.domain.entity.Paciente;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-10-11T18:56:41-0500",
    comments = "version: 1.5.5.Final, compiler: Eclipse JDT (IDE) 3.46.100.v20260826-1225, environment: Java 21.0.12.1 (Eclipse Adoptium)"
)
@Component
public class PacienteMapperImpl implements PacienteMapper {

    @Override
    public PacienteResponse toResponse(Paciente paciente) {
        if ( paciente == null ) {
            return null;
        }

        PacienteResponse.PacienteResponseBuilder pacienteResponse = PacienteResponse.builder();

        pacienteResponse.id( paciente.getId() );
        pacienteResponse.usuarioId( paciente.getUsuarioId() );
        pacienteResponse.nombre( paciente.getNombre() );
        pacienteResponse.apellido( paciente.getApellido() );
        pacienteResponse.email( paciente.getEmail() );
        pacienteResponse.dni( paciente.getDni() );
        pacienteResponse.fechaNacimiento( paciente.getFechaNacimiento() );
        pacienteResponse.sexo( paciente.getSexo() );
        pacienteResponse.telefono( paciente.getTelefono() );
        pacienteResponse.direccion( paciente.getDireccion() );
        pacienteResponse.tipoSangre( paciente.getTipoSangre() );
        pacienteResponse.alergias( paciente.getAlergias() );
        pacienteResponse.activo( paciente.isActivo() );

        return pacienteResponse.build();
    }
}
