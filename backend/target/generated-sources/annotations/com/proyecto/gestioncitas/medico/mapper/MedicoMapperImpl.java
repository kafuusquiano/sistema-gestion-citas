package com.proyecto.gestioncitas.medico.mapper;

import com.proyecto.gestioncitas.medico.domain.dto.response.EspecialidadResponse;
import com.proyecto.gestioncitas.medico.domain.dto.response.HorarioResponse;
import com.proyecto.gestioncitas.medico.domain.dto.response.MedicoResponse;
import com.proyecto.gestioncitas.medico.domain.entity.Especialidad;
import com.proyecto.gestioncitas.medico.domain.entity.HorarioMedico;
import com.proyecto.gestioncitas.medico.domain.entity.Medico;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-10-11T18:59:22-0500",
    comments = "version: 1.5.5.Final, compiler: Eclipse JDT (IDE) 3.46.100.v20260826-1225, environment: Java 21.0.12.1 (Eclipse Adoptium)"
)
@Component
public class MedicoMapperImpl implements MedicoMapper {

    @Override
    public MedicoResponse toResponse(Medico medico) {
        if ( medico == null ) {
            return null;
        }

        MedicoResponse.MedicoResponseBuilder medicoResponse = MedicoResponse.builder();

        medicoResponse.especialidadId( medicoEspecialidadId( medico ) );
        medicoResponse.especialidadNombre( medicoEspecialidadNombre( medico ) );
        medicoResponse.id( medico.getId() );
        medicoResponse.usuarioId( medico.getUsuarioId() );
        medicoResponse.nombre( medico.getNombre() );
        medicoResponse.apellido( medico.getApellido() );
        medicoResponse.email( medico.getEmail() );
        medicoResponse.dni( medico.getDni() );
        medicoResponse.codigoCmp( medico.getCodigoCmp() );
        medicoResponse.telefono( medico.getTelefono() );
        medicoResponse.estado( medico.getEstado() );

        return medicoResponse.build();
    }

    @Override
    public HorarioResponse toHorarioResponse(HorarioMedico horario) {
        if ( horario == null ) {
            return null;
        }

        HorarioResponse.HorarioResponseBuilder horarioResponse = HorarioResponse.builder();

        horarioResponse.medicoId( horarioMedicoId( horario ) );
        horarioResponse.id( horario.getId() );
        horarioResponse.diaSemana( horario.getDiaSemana() );
        horarioResponse.horaInicio( horario.getHoraInicio() );
        horarioResponse.horaFin( horario.getHoraFin() );
        horarioResponse.duracionCitaMin( horario.getDuracionCitaMin() );
        horarioResponse.activo( horario.isActivo() );

        return horarioResponse.build();
    }

    @Override
    public EspecialidadResponse toEspecialidadResponse(Especialidad especialidad) {
        if ( especialidad == null ) {
            return null;
        }

        EspecialidadResponse.EspecialidadResponseBuilder especialidadResponse = EspecialidadResponse.builder();

        especialidadResponse.id( especialidad.getId() );
        especialidadResponse.nombre( especialidad.getNombre() );
        especialidadResponse.descripcion( especialidad.getDescripcion() );
        especialidadResponse.activa( especialidad.isActiva() );

        return especialidadResponse.build();
    }

    private String medicoEspecialidadId(Medico medico) {
        if ( medico == null ) {
            return null;
        }
        Especialidad especialidad = medico.getEspecialidad();
        if ( especialidad == null ) {
            return null;
        }
        String id = especialidad.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }

    private String medicoEspecialidadNombre(Medico medico) {
        if ( medico == null ) {
            return null;
        }
        Especialidad especialidad = medico.getEspecialidad();
        if ( especialidad == null ) {
            return null;
        }
        String nombre = especialidad.getNombre();
        if ( nombre == null ) {
            return null;
        }
        return nombre;
    }

    private String horarioMedicoId(HorarioMedico horarioMedico) {
        if ( horarioMedico == null ) {
            return null;
        }
        Medico medico = horarioMedico.getMedico();
        if ( medico == null ) {
            return null;
        }
        String id = medico.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }
}
