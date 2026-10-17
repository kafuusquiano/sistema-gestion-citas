package com.proyecto.gestioncitas.medico.controller;

import com.proyecto.gestioncitas.common.dto.ApiResponse;
import com.proyecto.gestioncitas.medico.domain.dto.request.EspecialidadRequest;
import com.proyecto.gestioncitas.medico.domain.dto.response.EspecialidadResponse;
import com.proyecto.gestioncitas.medico.service.EspecialidadService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
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
import org.springframework.web.bind.annotation.RestController;

/**
 * Catalogo de especialidades. Cualquiera autenticado puede leerlo, porque el
 * paciente lo necesita para elegir el servicio al agendar.
 */
@RestController
@RequestMapping("/api/especialidades")
@Tag(name = "Especialidades", description = "Catalogo de especialidades del establecimiento")
public class EspecialidadController {

    private final EspecialidadService especialidadService;

    public EspecialidadController(EspecialidadService especialidadService) {
        this.especialidadService = especialidadService;
    }

    @GetMapping
    @Operation(summary = "Listar especialidades activas",
            description = "Respuesta cacheada 6 horas: el catalogo cambia muy poco.")
    public ResponseEntity<ApiResponse<List<EspecialidadResponse>>> listarActivas() {
        return ResponseEntity.ok(ApiResponse.exito(especialidadService.listarActivas()));
    }

    @GetMapping("/todas")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Listar todas, incluidas las desactivadas")
    public ResponseEntity<ApiResponse<List<EspecialidadResponse>>> listarTodas() {
        return ResponseEntity.ok(ApiResponse.exito(especialidadService.listarTodas()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Ver una especialidad")
    public ResponseEntity<ApiResponse<EspecialidadResponse>> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.exito(especialidadService.obtener(id)));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Crear una especialidad")
    public ResponseEntity<ApiResponse<EspecialidadResponse>> crear(
            @Valid @RequestBody EspecialidadRequest peticion) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.exito("Especialidad creada",
                        especialidadService.crear(peticion)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Actualizar una especialidad")
    public ResponseEntity<ApiResponse<EspecialidadResponse>> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody EspecialidadRequest peticion) {

        return ResponseEntity.ok(ApiResponse.exito("Especialidad actualizada",
                especialidadService.actualizar(id, peticion)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Desactivar una especialidad",
            description = """
                    No borra: hay medicos y citas historicas apuntando a ella y la
                    clave ajena es ON DELETE RESTRICT. Falla con 409 si quedan
                    medicos activos en la especialidad.
                    """)
    public ResponseEntity<ApiResponse<Void>> desactivar(@PathVariable Long id) {
        especialidadService.desactivar(id);
        return ResponseEntity.ok(ApiResponse.exito("Especialidad desactivada"));
    }

    @PatchMapping("/{id}/activar")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Reactivar una especialidad")
    public ResponseEntity<ApiResponse<EspecialidadResponse>> activar(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.exito("Especialidad reactivada",
                especialidadService.activar(id)));
    }
}
