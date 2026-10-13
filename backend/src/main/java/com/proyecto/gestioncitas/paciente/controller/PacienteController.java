package com.proyecto.gestioncitas.paciente.controller;

import com.proyecto.gestioncitas.common.constants.AppConstants;
import com.proyecto.gestioncitas.common.dto.ApiResponse;
import com.proyecto.gestioncitas.common.dto.PageResponse;
import com.proyecto.gestioncitas.common.util.SecurityUtils;
import com.proyecto.gestioncitas.paciente.domain.dto.request.ActualizarPacienteRequest;
import com.proyecto.gestioncitas.paciente.domain.dto.request.CrearPacienteRequest;
import com.proyecto.gestioncitas.paciente.domain.dto.request.PacienteFiltroRequest;
import com.proyecto.gestioncitas.paciente.domain.dto.response.PacienteResponse;
import com.proyecto.gestioncitas.paciente.domain.dto.response.PacienteResumenResponse;
import com.proyecto.gestioncitas.paciente.domain.enums.EstadoPaciente;
import com.proyecto.gestioncitas.paciente.domain.enums.Seguro;
import com.proyecto.gestioncitas.paciente.domain.enums.Sexo;
import com.proyecto.gestioncitas.paciente.service.PacienteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Registro y consulta de pacientes (objetivo especifico 1).
 *
 * Permisos de la seccion 2.4: ADMIN lee y escribe, MEDICO solo lee, y el
 * PACIENTE solo su propio perfil por /me.
 */
@RestController
@RequestMapping("/api/pacientes")
@Tag(name = "Pacientes", description = "Registro, busqueda y ficha del paciente")
public class PacienteController {

    private final PacienteService pacienteService;

    public PacienteController(PacienteService pacienteService) {
        this.pacienteService = pacienteService;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Registrar un paciente desde admision",
            description = """
                    Para quien llega por ventanilla, con o sin cuenta de acceso.
                    El numero de historia clinica lo asigna el sistema.
                    """)
    public ResponseEntity<ApiResponse<PacienteResponse>> crear(
            @Valid @RequestBody CrearPacienteRequest peticion) {

        PacienteResponse creado = pacienteService.crear(peticion);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.exito("Paciente registrado correctamente", creado));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MEDICO')")
    @Operation(summary = "Buscar pacientes",
            description = """
                    El parametro texto es una sola caja de busqueda que cruza
                    documento, nombres, apellidos y numero de historia, para que
                    admision no tenga que elegir primero por que campo buscar.
                    """)
    public ResponseEntity<ApiResponse<PageResponse<PacienteResumenResponse>>> buscar(
            @RequestParam(required = false) String texto,
            @RequestParam(required = false) EstadoPaciente estado,
            @RequestParam(required = false) Sexo sexo,
            @RequestParam(required = false) Seguro seguro,
            @RequestParam(required = false) String distrito,
            @RequestParam(required = false) Integer edadMinima,
            @RequestParam(required = false) Integer edadMaxima,
            @RequestParam(required = false) Boolean conCuenta,
            @RequestParam(defaultValue = "0") @Min(0) int pagina,
            @RequestParam(defaultValue = "20") @Min(1) @Max(AppConstants.TAMANO_PAGINA_MAXIMO) int tamano) {

        var filtro = new PacienteFiltroRequest(texto, estado, sexo, seguro, distrito,
                edadMinima, edadMaxima, conCuenta);
        var pageable = PageRequest.of(pagina, tamano,
                Sort.by(Sort.Direction.ASC, "apellidoPaterno", "apellidoMaterno", "nombres"));

        return ResponseEntity.ok(ApiResponse.exito(pacienteService.buscar(filtro, pageable)));
    }

    @GetMapping("/me")
    @Operation(summary = "Ver mi propia ficha",
            description = "Responde 404 si la cuenta no tiene ficha, como ocurre con un ADMIN.")
    public ResponseEntity<ApiResponse<PacienteResponse>> verMiFicha() {
        return ResponseEntity.ok(ApiResponse.exito(
                pacienteService.obtenerPorUsuario(usuarioAutenticado())));
    }

    @PutMapping("/me")
    @Operation(summary = "Actualizar mi propia ficha",
            description = "El paciente puede corregir sus datos de contacto y clinicos.")
    public ResponseEntity<ApiResponse<PacienteResponse>> actualizarMiFicha(
            @Valid @RequestBody ActualizarPacienteRequest peticion) {

        PacienteResponse propia = pacienteService.obtenerPorUsuario(usuarioAutenticado());
        var actualizado = pacienteService.actualizar(propia.id(), peticion);
        return ResponseEntity.ok(ApiResponse.exito("Ficha actualizada", actualizado));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MEDICO')")
    @Operation(summary = "Ver la ficha completa de un paciente")
    public ResponseEntity<ApiResponse<PacienteResponse>> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.exito(pacienteService.obtener(id)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Actualizar la ficha de un paciente",
            description = "No cambia el documento ni el numero de historia.")
    public ResponseEntity<ApiResponse<PacienteResponse>> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody ActualizarPacienteRequest peticion) {

        return ResponseEntity.ok(ApiResponse.exito("Ficha actualizada",
                pacienteService.actualizar(id, peticion)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Dar de baja a un paciente",
            description = """
                    Pasa la ficha a INACTIVO; no borra nada. El historial clinico
                    debe conservarse por la trazabilidad del objetivo 3, y un
                    paciente inactivo no puede agendar citas nuevas.
                    """)
    public ResponseEntity<ApiResponse<Void>> darDeBaja(@PathVariable Long id) {
        pacienteService.darDeBaja(id);
        return ResponseEntity.ok(ApiResponse.exito("Paciente dado de baja"));
    }

    @PatchMapping("/{id}/reactivar")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Reactivar a un paciente dado de baja")
    public ResponseEntity<ApiResponse<PacienteResponse>> reactivar(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.exito("Paciente reactivado",
                pacienteService.reactivar(id)));
    }

    private Long usuarioAutenticado() {
        return SecurityUtils.obtenerUsuarioActual()
                .orElseThrow(() -> new IllegalStateException(
                        "Ruta autenticada sin usuario en el contexto"));
    }
}
