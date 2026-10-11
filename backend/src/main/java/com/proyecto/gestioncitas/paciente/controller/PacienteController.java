package com.proyecto.gestioncitas.paciente.controller;

import com.proyecto.gestioncitas.common.dto.ApiResponse;
import com.proyecto.gestioncitas.common.dto.PageResponse;
import com.proyecto.gestioncitas.paciente.domain.dto.request.*;
import com.proyecto.gestioncitas.paciente.domain.dto.response.PacienteResponse;
import com.proyecto.gestioncitas.paciente.service.PacienteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/pacientes")
@RequiredArgsConstructor
@Tag(name = "Pacientes", description = "Gestión de pacientes")
public class PacienteController {

    private final PacienteService pacienteService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Registrar paciente")
    public ResponseEntity<ApiResponse<PacienteResponse>> crear(
            @Valid @RequestBody CrearPacienteRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.<PacienteResponse>builder()
                .success(true)
                .message("Paciente registrado correctamente")
                .data(pacienteService.crear(request)).build());
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','MEDICO')")
    @Operation(summary = "Listar pacientes")
    public ResponseEntity<ApiResponse<PageResponse<PacienteResponse>>> listar(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(ApiResponse.<PageResponse<PacienteResponse>>builder()
            .success(true).data(pacienteService.listar(page, size)).build());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','MEDICO') or #id == authentication.name")
    @Operation(summary = "Obtener paciente por ID")
    public ResponseEntity<ApiResponse<PacienteResponse>> obtener(@PathVariable String id) {
        return ResponseEntity.ok(ApiResponse.<PacienteResponse>builder()
            .success(true).data(pacienteService.obtenerPorId(id)).build());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or #id == authentication.name")
    @Operation(summary = "Actualizar datos del paciente")
    public ResponseEntity<ApiResponse<PacienteResponse>> actualizar(
            @PathVariable String id,
            @Valid @RequestBody ActualizarPacienteRequest request) {
        return ResponseEntity.ok(ApiResponse.<PacienteResponse>builder()
            .success(true)
            .message("Paciente actualizado correctamente")
            .data(pacienteService.actualizar(id, request)).build());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Desactivar paciente")
    public ResponseEntity<ApiResponse<Void>> desactivar(@PathVariable String id) {
        pacienteService.desactivar(id);
        return ResponseEntity.ok(ApiResponse.<Void>builder()
            .success(true).message("Paciente desactivado").build());
    }
}
