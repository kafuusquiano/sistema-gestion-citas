package com.proyecto.gestioncitas.medico.controller;

import com.proyecto.gestioncitas.common.dto.ApiResponse;
import com.proyecto.gestioncitas.medico.domain.dto.response.EspecialidadResponse;
import com.proyecto.gestioncitas.medico.service.EspecialidadService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/especialidades")
@RequiredArgsConstructor
@Tag(name = "Especialidades", description = "Gestión de especialidades médicas")
public class EspecialidadController {

    private final EspecialidadService especialidadService;

    @GetMapping
    @Operation(summary = "Listar especialidades activas")
    public ResponseEntity<ApiResponse<List<EspecialidadResponse>>> listar() {
        return ResponseEntity.ok(ApiResponse.<List<EspecialidadResponse>>builder()
            .success(true).data(especialidadService.listarActivas()).build());
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Crear especialidad")
    public ResponseEntity<ApiResponse<EspecialidadResponse>> crear(
            @RequestParam String nombre,
            @RequestParam(required = false) String descripcion) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.<EspecialidadResponse>builder()
                .success(true).message("Especialidad creada")
                .data(especialidadService.crear(nombre, descripcion)).build());
    }
}
