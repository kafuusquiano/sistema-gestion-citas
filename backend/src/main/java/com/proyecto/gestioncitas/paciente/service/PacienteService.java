package com.proyecto.gestioncitas.paciente.service;

import com.proyecto.gestioncitas.common.dto.PageResponse;
import com.proyecto.gestioncitas.paciente.domain.dto.request.ActualizarPacienteRequest;
import com.proyecto.gestioncitas.paciente.domain.dto.request.CrearPacienteRequest;
import com.proyecto.gestioncitas.paciente.domain.dto.request.PacienteFiltroRequest;
import com.proyecto.gestioncitas.paciente.domain.dto.response.PacienteResponse;
import com.proyecto.gestioncitas.paciente.domain.dto.response.PacienteResumenResponse;
import java.time.LocalDate;
import org.springframework.data.domain.Pageable;

/**
 * Registro y consulta de pacientes (objetivo especifico 1).
 */
public interface PacienteService {

    /** Alta desde admision. Asigna el numero de historia. */
    PacienteResponse crear(CrearPacienteRequest peticion);

    PageResponse<PacienteResumenResponse> buscar(PacienteFiltroRequest filtro, Pageable pageable);

    PacienteResponse obtener(Long id);

    /** Ficha del paciente autenticado, para la pantalla "mi perfil". */
    PacienteResponse obtenerPorUsuario(Long usuarioId);

    PacienteResponse actualizar(Long id, ActualizarPacienteRequest peticion);

    /** Pasa la ficha a INACTIVO. Nunca borra: el historial clinico debe quedar. */
    void darDeBaja(Long id);

    PacienteResponse reactivar(Long id);

    /**
     * Crea la ficha a partir de un registro publico.
     *
     * La llama el listener de usuario.registrado, en la misma transaccion que
     * creo la cuenta, de modo que el registro es atomico: o quedan cuenta y
     * ficha, o no queda ninguna.
     */
    void crearDesdeRegistro(Long usuarioId, String numeroDocumento, String nombres,
            String apellidoPaterno, String apellidoMaterno, LocalDate fechaNacimiento,
            String sexo, String telefono, String email);
}
