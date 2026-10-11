package com.proyecto.gestioncitas.paciente.service;

import com.proyecto.gestioncitas.paciente.domain.dto.request.*;
import com.proyecto.gestioncitas.paciente.domain.dto.response.PacienteResponse;
import com.proyecto.gestioncitas.common.dto.PageResponse;

public interface PacienteService {
    PacienteResponse crear(CrearPacienteRequest request);
    PacienteResponse obtenerPorId(String id);
    PacienteResponse obtenerPorUsuarioId(String usuarioId);
    PageResponse<PacienteResponse> listar(int page, int size);
    PacienteResponse actualizar(String id, ActualizarPacienteRequest request);
    void desactivar(String id);
}
