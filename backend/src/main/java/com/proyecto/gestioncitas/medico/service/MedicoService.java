package com.proyecto.gestioncitas.medico.service;

import com.proyecto.gestioncitas.common.dto.PageResponse;
import com.proyecto.gestioncitas.medico.domain.dto.request.ActualizarMedicoRequest;
import com.proyecto.gestioncitas.medico.domain.dto.request.CrearMedicoRequest;
import com.proyecto.gestioncitas.medico.domain.dto.response.MedicoResponse;
import com.proyecto.gestioncitas.medico.domain.dto.response.MedicoResumenResponse;
import com.proyecto.gestioncitas.medico.domain.entity.Medico;
import com.proyecto.gestioncitas.medico.domain.enums.EstadoMedico;
import java.util.List;
import org.springframework.data.domain.Pageable;

/**
 * Alta y gestion del personal medico.
 */
public interface MedicoService {

    /**
     * Da de alta un medico y su cuenta de acceso.
     *
     * Segun el tipo construye un MedicoGeneral o un MedicoEspecialista. La
     * cuenta se pide por AuthFacade, no instanciando un Usuario.
     */
    MedicoResponse crear(CrearMedicoRequest peticion);

    PageResponse<MedicoResumenResponse> buscar(Long especialidadId, EstadoMedico estado,
            String texto, Pageable pageable);

    /** Medicos activos de una especialidad, para el selector al agendar. */
    List<MedicoResumenResponse> listarActivosPorEspecialidad(Long especialidadId);

    MedicoResponse obtener(Long id);

    /** Registro del medico autenticado, para su propia agenda. */
    MedicoResponse obtenerPorUsuario(Long usuarioId);

    MedicoResponse actualizar(Long id, ActualizarMedicoRequest peticion);

    /**
     * Cambia el estado. Al salir de ACTIVO publica
     * medico.horario-actualizado, porque el medico deja de ofrecer cupos.
     */
    MedicoResponse cambiarEstado(Long id, EstadoMedico estado);

    /** Para uso interno del modulo. */
    Medico obtenerEntidad(Long id);
}
