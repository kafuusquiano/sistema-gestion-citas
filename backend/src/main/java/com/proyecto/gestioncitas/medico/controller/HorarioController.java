package com.proyecto.gestioncitas.medico.controller;

import com.proyecto.gestioncitas.common.dto.ApiResponse;
import com.proyecto.gestioncitas.medico.domain.dto.request.ExcepcionHorarioRequest;
import com.proyecto.gestioncitas.medico.domain.dto.request.HorarioRequest;
import com.proyecto.gestioncitas.medico.domain.dto.response.ExcepcionHorarioResponse;
import com.proyecto.gestioncitas.medico.domain.dto.response.HorarioResponse;
import com.proyecto.gestioncitas.medico.service.HorarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Horarios de atencion y excepciones.
 *
 * Segun la matriz de la seccion 2.4, el ADMIN administra todos los horarios y
 * el MEDICO el suyo. Esa segunda parte se resuelve con un @PreAuthorize que
 * compara el medico de la ruta con el del token, via la expresion de seguridad
 * sobre MedicoFacade.
 */
@RestController
@RequestMapping("/api/medicos")
@Tag(name = "Horarios", description = "Plantilla semanal de atencion y excepciones")
public class HorarioController {

    private final HorarioService horarioService;

    public HorarioController(HorarioService horarioService) {
        this.horarioService = horarioService;
    }

    @GetMapping("/{medicoId}/horarios")
    @Operation(summary = "Ver el horario semanal de un medico",
            description = "Respuesta cacheada 30 minutos (instruccion M4).")
    public ResponseEntity<ApiResponse<List<HorarioResponse>>> obtener(
            @PathVariable Long medicoId) {

        return ResponseEntity.ok(ApiResponse.exito(horarioService.obtenerHorarios(medicoId)));
    }

    @PutMapping("/{medicoId}/horarios")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Reemplazar el horario semanal",
            description = """
                    Reemplaza la plantilla completa, no agrega franjas sueltas: la
                    pantalla edita la semana entera y enviar solo los cambios
                    obligaria a calcular diferencias en el cliente.

                    Rechaza con 409 dos franjas cruzadas del mismo dia. Publica
                    medico.horario-actualizado para invalidar la cache de slots.
                    """)
    public ResponseEntity<ApiResponse<List<HorarioResponse>>> reemplazar(
            @PathVariable Long medicoId,
            @Valid @NotEmpty(message = "Debe enviar al menos una franja")
            @RequestBody List<@Valid HorarioRequest> horarios) {

        return ResponseEntity.ok(ApiResponse.exito("Horario actualizado",
                horarioService.reemplazarHorarios(medicoId, horarios)));
    }

    @GetMapping("/{medicoId}/excepciones")
    @Operation(summary = "Listar las excepciones de un medico")
    public ResponseEntity<ApiResponse<List<ExcepcionHorarioResponse>>> listarExcepciones(
            @PathVariable Long medicoId) {

        return ResponseEntity.ok(ApiResponse.exito(horarioService.listarExcepciones(medicoId)));
    }

    @PostMapping("/{medicoId}/excepciones")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Registrar vacaciones, licencia o capacitacion",
            description = "Un dia libre se registra con la misma fecha en los dos extremos.")
    public ResponseEntity<ApiResponse<ExcepcionHorarioResponse>> registrarExcepcion(
            @PathVariable Long medicoId,
            @Valid @RequestBody ExcepcionHorarioRequest peticion) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.exito("Excepcion registrada",
                        horarioService.registrarExcepcion(medicoId, peticion)));
    }

    @GetMapping("/excepciones/generales")
    @Operation(summary = "Listar los feriados del establecimiento")
    public ResponseEntity<ApiResponse<List<ExcepcionHorarioResponse>>> listarGenerales() {
        return ResponseEntity.ok(ApiResponse.exito(horarioService.listarExcepcionesGenerales()));
    }

    @PostMapping("/excepciones/generales")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Registrar un feriado del establecimiento",
            description = """
                    Aplica a todos los medicos a la vez, asi no hay que repetir la
                    misma fila por cada uno. Invalida la cache de horarios completa.
                    """)
    public ResponseEntity<ApiResponse<ExcepcionHorarioResponse>> registrarGeneral(
            @Valid @RequestBody ExcepcionHorarioRequest peticion) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.exito("Feriado registrado",
                        horarioService.registrarExcepcionGeneral(peticion)));
    }

    @DeleteMapping("/excepciones/{excepcionId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Eliminar una excepcion")
    public ResponseEntity<ApiResponse<Void>> eliminarExcepcion(@PathVariable Long excepcionId) {
        horarioService.eliminarExcepcion(excepcionId);
        return ResponseEntity.ok(ApiResponse.exito("Excepcion eliminada"));
    }
}
