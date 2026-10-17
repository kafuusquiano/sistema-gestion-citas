package com.proyecto.gestioncitas.medico.service;

import com.proyecto.gestioncitas.medico.domain.dto.request.EspecialidadRequest;
import com.proyecto.gestioncitas.medico.domain.dto.response.EspecialidadResponse;
import com.proyecto.gestioncitas.medico.domain.entity.Especialidad;
import java.util.List;

/**
 * Catalogo de especialidades del establecimiento.
 */
public interface EspecialidadService {

    EspecialidadResponse crear(EspecialidadRequest peticion);

    EspecialidadResponse actualizar(Long id, EspecialidadRequest peticion);

    /** Solo las activas, para los selectores. Cacheado 6 horas. */
    List<EspecialidadResponse> listarActivas();

    List<EspecialidadResponse> listarTodas();

    EspecialidadResponse obtener(Long id);

    /**
     * Desactiva la especialidad. No borra: hay medicos y citas historicas
     * apuntando a ella y la FK es ON DELETE RESTRICT.
     */
    void desactivar(Long id);

    EspecialidadResponse activar(Long id);

    /** Para uso interno del modulo, no se expone por el controller. */
    Especialidad obtenerEntidad(Long id);
}
