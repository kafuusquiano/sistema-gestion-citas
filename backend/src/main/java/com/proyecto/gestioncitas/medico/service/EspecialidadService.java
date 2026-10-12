package com.proyecto.gestioncitas.medico.service;

import com.proyecto.gestioncitas.medico.domain.dto.response.EspecialidadResponse;
import java.util.List;

public interface EspecialidadService {
    List<EspecialidadResponse> listarActivas();
    EspecialidadResponse crear(String nombre, String descripcion);
}
