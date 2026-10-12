package com.proyecto.gestioncitas.medico.service;

import com.proyecto.gestioncitas.common.dto.PageResponse;
import com.proyecto.gestioncitas.medico.domain.dto.request.*;
import com.proyecto.gestioncitas.medico.domain.dto.response.*;
import java.time.LocalDate;
import java.util.List;

public interface MedicoService {
    MedicoResponse crear(CrearMedicoRequest request);
    MedicoResponse obtenerPorId(String id);
    PageResponse<MedicoResponse> listar(int page, int size);
    List<MedicoResponse> listarPorEspecialidad(String especialidadId);
    MedicoResponse actualizar(String id, ActualizarMedicoRequest request);
    HorarioResponse agregarHorario(String medicoId, HorarioRequest request);
    List<HorarioResponse> obtenerHorarios(String medicoId);
    List<SlotDisponibleResponse> obtenerSlotsDisponibles(String medicoId, LocalDate fecha);
}
