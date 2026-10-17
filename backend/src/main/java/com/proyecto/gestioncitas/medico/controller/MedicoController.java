package com.proyecto.gestioncitas.medico.controller;

import com.proyecto.gestioncitas.common.constants.AppConstants;
import com.proyecto.gestioncitas.common.dto.ApiResponse;
import com.proyecto.gestioncitas.common.dto.PageResponse;
import com.proyecto.gestioncitas.common.util.SecurityUtils;
import com.proyecto.gestioncitas.medico.domain.dto.request.ActualizarMedicoRequest;
import com.proyecto.gestioncitas.medico.domain.dto.request.CrearMedicoRequest;
import com.proyecto.gestioncitas.medico.domain.dto.response.MedicoResponse;
import com.proyecto.gestioncitas.medico.domain.dto.response.MedicoResumenResponse;
import com.proyecto.gestioncitas.medico.domain.enums.EstadoMedico;
import com.proyecto.gestioncitas.medico.service.MedicoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
 * Personal medico del establecimiento.
 *
 * Permisos de la seccion 2.4: ADMIN lee y escribe, el PACIENTE solo lee para
 * poder elegir con quien atenderse, y el MEDICO consulta su propio registro
 * por /me.
 */
@RestController
@RequestMapping("/api/medicos")
@Tag(name = "Medicos", description = "Alta y gestion del personal medico")
public class MedicoController {

    private final MedicoService medicoService;

    public MedicoController(MedicoService medicoService) {
        this.medicoService = medicoService;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Dar de alta un medico",
            description = """
                    Crea la cuenta de acceso y el registro profesional. Segun el
                    campo tipo construye un MedicoGeneral o un MedicoEspecialista:
                    el ESPECIALISTA requiere RNE y el GENERAL no debe tenerlo.

                    No se usa el registro publico porque hay que verificar el CMP.
                    """)
    public ResponseEntity<ApiResponse<MedicoResponse>> crear(
            @Valid @RequestBody CrearMedicoRequest peticion) {

        MedicoResponse creado = medicoService.crear(peticion);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.exito("Medico dado de alta correctamente", creado));
    }

    @GetMapping
    @Operation(summary = "Buscar medicos",
            description = "Filtros por especialidad, estado y texto sobre nombres o CMP.")
    public ResponseEntity<ApiResponse<PageResponse<MedicoResumenResponse>>> buscar(
            @RequestParam(required = false) Long especialidadId,
            @RequestParam(required = false) EstadoMedico estado,
            @RequestParam(required = false) String texto,
            @RequestParam(defaultValue = "0") @Min(0) int pagina,
            @RequestParam(defaultValue = "20") @Min(1) @Max(AppConstants.TAMANO_PAGINA_MAXIMO) int tamano) {

        var pageable = PageRequest.of(pagina, tamano,
                Sort.by(Sort.Direction.ASC, "apellidoPaterno", "nombres"));
        return ResponseEntity.ok(ApiResponse.exito(
                medicoService.buscar(especialidadId, estado, texto, pageable)));
    }

    @GetMapping("/disponibles")
    @Operation(summary = "Medicos activos de una especialidad",
            description = "Lo que alimenta el selector de medico al agendar una cita.")
    public ResponseEntity<ApiResponse<List<MedicoResumenResponse>>> listarActivos(
            @RequestParam @NotNull Long especialidadId) {

        return ResponseEntity.ok(ApiResponse.exito(
                medicoService.listarActivosPorEspecialidad(especialidadId)));
    }

    @GetMapping("/me")
    @PreAuthorize("hasRole('MEDICO')")
    @Operation(summary = "Ver mi propio registro profesional")
    public ResponseEntity<ApiResponse<MedicoResponse>> verMiRegistro() {
        Long usuarioId = SecurityUtils.obtenerUsuarioActual()
                .orElseThrow(() -> new IllegalStateException(
                        "Ruta autenticada sin usuario en el contexto"));
        return ResponseEntity.ok(ApiResponse.exito(medicoService.obtenerPorUsuario(usuarioId)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Ver la ficha de un medico con sus horarios")
    public ResponseEntity<ApiResponse<MedicoResponse>> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.exito(medicoService.obtener(id)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Actualizar un medico",
            description = """
                    No cambia el CMP ni el tipo. Cambiar el tipo significaria
                    cambiar la clase de la fila, y en SINGLE_TABLE eso obliga a
                    reescribir el discriminador, que JPA no permite.
                    """)
    public ResponseEntity<ApiResponse<MedicoResponse>> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody ActualizarMedicoRequest peticion) {

        return ResponseEntity.ok(ApiResponse.exito("Medico actualizado",
                medicoService.actualizar(id, peticion)));
    }

    @PatchMapping("/{id}/estado")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Cambiar el estado de un medico",
            description = """
                    LICENCIA conserva sus citas agendadas pero no admite nuevas.
                    INACTIVO es la baja definitiva. El cambio invalida la cache de
                    disponibilidad.
                    """)
    public ResponseEntity<ApiResponse<MedicoResponse>> cambiarEstado(
            @PathVariable Long id,
            @RequestParam @NotNull EstadoMedico estado) {

        return ResponseEntity.ok(ApiResponse.exito("Estado actualizado",
                medicoService.cambiarEstado(id, estado)));
    }
}
