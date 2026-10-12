package com.proyecto.gestioncitas.medico.controller;

import com.proyecto.gestioncitas.common.dto.ApiResponse;
import com.proyecto.gestioncitas.common.dto.PageResponse;
import com.proyecto.gestioncitas.medico.domain.dto.request.*;
import com.proyecto.gestioncitas.medico.domain.dto.response.*;
import com.proyecto.gestioncitas.medico.service.MedicoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/medicos")
@RequiredArgsConstructor
@Tag(name = "Médicos", description = "Gestión de médicos y horarios")
public class MedicoController {

    private final MedicoService medicoService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Registrar médico")
    public ResponseEntity<ApiResponse<MedicoResponse>> crear(
            @Valid @RequestBody CrearMedicoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.<MedicoResponse>builder()
                .success(true).message("Médico registrado correctamente")
                .data(medicoService.crear(request)).build());
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','PACIENTE')")
    @Operation(summary = "Listar médicos")
    public ResponseEntity<ApiResponse<PageResponse<MedicoResponse>>> listar(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(ApiResponse.<PageResponse<MedicoResponse>>builder()
            .success(true).data(medicoService.listar(page, size)).build());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','MEDICO','PACIENTE')")
    @Operation(summary = "Obtener médico por ID")
    public ResponseEntity<ApiResponse<MedicoResponse>> obtener(@PathVariable String id) {
        return ResponseEntity.ok(ApiResponse.<MedicoResponse>builder()
            .success(true).data(medicoService.obtenerPorId(id)).build());
    }

    @GetMapping("/especialidad/{especialidadId}")
    @PreAuthorize("hasAnyRole('ADMIN','PACIENTE')")
    @Operation(summary = "Listar médicos por especialidad")
    public ResponseEntity<ApiResponse<List<MedicoResponse>>> porEspecialidad(
            @PathVariable String especialidadId) {
        return ResponseEntity.ok(ApiResponse.<List<MedicoResponse>>builder()
            .success(true).data(medicoService.listarPorEspecialidad(especialidadId)).build());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or #id == authentication.name")
    @Operation(summary = "Actualizar médico")
    public ResponseEntity<ApiResponse<MedicoResponse>> actualizar(
            @PathVariable String id,
            @Valid @RequestBody ActualizarMedicoRequest request) {
        return ResponseEntity.ok(ApiResponse.<MedicoResponse>builder()
            .success(true).message("Médico actualizado").data(medicoService.actualizar(id, request)).build());
    }

    @PostMapping("/{id}/horarios")
    @PreAuthorize("hasRole('ADMIN') or #id == authentication.name")
    @Operation(summary = "Agregar horario al médico")
    public ResponseEntity<ApiResponse<HorarioResponse>> agregarHorario(
            @PathVariable String id,
            @Valid @RequestBody HorarioRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.<HorarioResponse>builder()
                .success(true).message("Horario agregado")
                .data(medicoService.agregarHorario(id, request)).build());
    }

    @GetMapping("/{id}/horarios")
    @PreAuthorize("hasAnyRole('ADMIN','MEDICO','PACIENTE')")
    @Operation(summary = "Obtener horarios del médico")
    public ResponseEntity<ApiResponse<List<HorarioResponse>>> horarios(@PathVariable String id) {
        return ResponseEntity.ok(ApiResponse.<List<HorarioResponse>>builder()
            .success(true).data(medicoService.obtenerHorarios(id)).build());
    }

    @GetMapping("/{id}/slots")
    @PreAuthorize("hasAnyRole('ADMIN','MEDICO','PACIENTE')")
    @Operation(summary = "Obtener slots disponibles por fecha")
    public ResponseEntity<ApiResponse<List<SlotDisponibleResponse>>> slots(
            @PathVariable String id,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
        return ResponseEntity.ok(ApiResponse.<List<SlotDisponibleResponse>>builder()
            .success(true).data(medicoService.obtenerSlotsDisponibles(id, fecha)).build());
    }
}
