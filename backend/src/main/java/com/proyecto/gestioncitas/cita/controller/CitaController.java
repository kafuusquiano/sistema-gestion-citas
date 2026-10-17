package com.proyecto.gestioncitas.cita.controller;

import com.proyecto.gestioncitas.cita.domain.dto.request.CancelarRequest;
import com.proyecto.gestioncitas.cita.domain.dto.request.CitaRequest;
import com.proyecto.gestioncitas.cita.domain.dto.request.ReprogramarRequest;
import com.proyecto.gestioncitas.cita.domain.dto.response.CitaResponse;
import com.proyecto.gestioncitas.cita.domain.dto.response.SlotDisponibleResponse;
import com.proyecto.gestioncitas.cita.domain.enums.EstadoCita;
import com.proyecto.gestioncitas.cita.service.CitaService;
import com.proyecto.gestioncitas.cita.service.DisponibilidadService;
import com.proyecto.gestioncitas.common.constants.AppConstants;
import com.proyecto.gestioncitas.common.dto.ApiResponse;
import com.proyecto.gestioncitas.common.dto.PageResponse;
import com.proyecto.gestioncitas.common.util.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.domain.PageRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Programacion y seguimiento de citas. Es el modulo central del sistema y
 * cubre los objetivos especificos 2, 3 y 4.
 *
 * Permisos de la seccion 2.4: el PACIENTE agenda, cancela y reprograma lo
 * suyo; el MEDICO confirma y completa sus citas; el ADMIN puede todo.
 */
@RestController
@RequestMapping("/api/citas")
@Tag(name = "Citas", description = "Disponibilidad, programacion y seguimiento de citas")
public class CitaController {

    private final CitaService citaService;
    private final DisponibilidadService disponibilidadService;

    public CitaController(CitaService citaService, DisponibilidadService disponibilidadService) {
        this.citaService = citaService;
        this.disponibilidadService = disponibilidadService;
    }

    @GetMapping("/disponibilidad")
    @Operation(summary = "Consultar turnos de un medico en una fecha",
            description = """
                    Proyecta la plantilla semanal del medico sobre la fecha, le
                    resta las excepciones y le quita las citas ya reservadas.

                    Devuelve tambien los turnos ocupados, marcados con
                    disponible en false, para que el calendario pueda mostrarlos
                    en gris. Con soloLibres en true se omiten.

                    Respuesta cacheada 5 minutos.
                    """)
    public ResponseEntity<ApiResponse<List<SlotDisponibleResponse>>> disponibilidad(
            @RequestParam @NotNull Long medicoId,
            @RequestParam @NotNull @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha,
            @RequestParam(defaultValue = "false") boolean soloLibres) {

        var slots = soloLibres
                ? disponibilidadService.obtenerSlotsLibres(medicoId, fecha)
                : disponibilidadService.obtenerSlots(medicoId, fecha);

        return ResponseEntity.ok(ApiResponse.exito(slots));
    }

    @PostMapping
    @Operation(summary = "Agendar una cita",
            description = """
                    Si no se envia pacienteId, se usa la ficha del usuario
                    autenticado. Un ADMIN si debe indicarlo, porque agenda en
                    nombre de otro.

                    Responde 409 si el turno ya fue reservado.
                    """)
    public ResponseEntity<ApiResponse<CitaResponse>> agendar(
            @Valid @RequestBody CitaRequest peticion) {

        CitaResponse creada = citaService.agendar(peticion, usuarioAutenticado());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.exito("Cita agendada correctamente", creada));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MEDICO')")
    @Operation(summary = "Buscar citas con filtros")
    public ResponseEntity<ApiResponse<PageResponse<CitaResponse>>> buscar(
            @RequestParam(required = false) Long pacienteId,
            @RequestParam(required = false) Long medicoId,
            @RequestParam(required = false) EstadoCita estado,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta,
            @RequestParam(defaultValue = "0") @Min(0) int pagina,
            @RequestParam(defaultValue = "20") @Min(1) @Max(AppConstants.TAMANO_PAGINA_MAXIMO) int tamano) {

        var pageable = PageRequest.of(pagina, tamano);
        return ResponseEntity.ok(ApiResponse.exito(
                citaService.buscar(pacienteId, medicoId, estado, desde, hasta, pageable)));
    }

    @GetMapping("/mis-citas")
    @Operation(summary = "Ver mis propias citas",
            description = "Citas del paciente autenticado, de la mas reciente a la mas antigua.")
    public ResponseEntity<ApiResponse<PageResponse<CitaResponse>>> misCitas(
            @RequestParam(defaultValue = "0") @Min(0) int pagina,
            @RequestParam(defaultValue = "20") @Min(1) @Max(AppConstants.TAMANO_PAGINA_MAXIMO) int tamano) {

        var pageable = PageRequest.of(pagina, tamano);
        return ResponseEntity.ok(ApiResponse.exito(
                citaService.misCitas(usuarioAutenticado(), pageable)));
    }

    @GetMapping("/agenda")
    @PreAuthorize("hasAnyRole('ADMIN', 'MEDICO')")
    @Operation(summary = "Agenda del dia de un medico")
    public ResponseEntity<ApiResponse<List<CitaResponse>>> agenda(
            @RequestParam @NotNull Long medicoId,
            @RequestParam @NotNull @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {

        return ResponseEntity.ok(ApiResponse.exito(
                citaService.agendaDelMedico(medicoId, fecha)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Ver una cita con su historial de cambios")
    public ResponseEntity<ApiResponse<CitaResponse>> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.exito(citaService.obtener(id)));
    }

    @PatchMapping("/{id}/confirmar")
    @Operation(summary = "Confirmar una cita",
            description = "La cita sigue ocupando el cupo; el paciente avisa que asistira.")
    public ResponseEntity<ApiResponse<CitaResponse>> confirmar(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.exito("Cita confirmada",
                citaService.confirmar(id, usuarioAutenticado())));
    }

    @PatchMapping("/{id}/cancelar")
    @Operation(summary = "Cancelar una cita",
            description = """
                    Libera el cupo para otro paciente. El paciente debe cancelar
                    con al menos 24 horas de anticipacion; ADMIN y MEDICO estan
                    exentos de ese plazo, y una cita de tipo EMERGENCIA tambien.
                    """)
    public ResponseEntity<ApiResponse<CitaResponse>> cancelar(
            @PathVariable Long id,
            @Valid @RequestBody CancelarRequest peticion) {

        boolean esPersonal = SecurityUtils.tieneRol("ADMIN") || SecurityUtils.tieneRol("MEDICO");
        return ResponseEntity.ok(ApiResponse.exito("Cita cancelada",
                citaService.cancelar(id, peticion, usuarioAutenticado(), esPersonal)));
    }

    @PatchMapping("/{id}/reprogramar")
    @Operation(summary = "Reprogramar una cita",
            description = """
                    Mueve la cita a otra fecha conservando su identificador. La
                    fecha anterior queda en el historial y la cita vuelve a
                    PROGRAMADA, porque el horario nuevo debe confirmarse.
                    """)
    public ResponseEntity<ApiResponse<CitaResponse>> reprogramar(
            @PathVariable Long id,
            @Valid @RequestBody ReprogramarRequest peticion) {

        return ResponseEntity.ok(ApiResponse.exito("Cita reprogramada",
                citaService.reprogramar(id, peticion, usuarioAutenticado())));
    }

    @PatchMapping("/{id}/completar")
    @PreAuthorize("hasAnyRole('ADMIN', 'MEDICO')")
    @Operation(summary = "Registrar la atencion realizada",
            description = "No se puede completar una cita cuya hora todavia no llega.")
    public ResponseEntity<ApiResponse<CitaResponse>> completar(
            @PathVariable Long id,
            @RequestParam(required = false) @Size(max = 500) String observaciones) {

        return ResponseEntity.ok(ApiResponse.exito("Atencion registrada",
                citaService.completar(id, observaciones, usuarioAutenticado())));
    }

    @PatchMapping("/{id}/no-asistio")
    @PreAuthorize("hasAnyRole('ADMIN', 'MEDICO')")
    @Operation(summary = "Marcar una inasistencia",
            description = """
                    Registro manual. Existe ademas un proceso automatico que
                    marca cada 15 minutos las citas vencidas que nadie atendio.
                    """)
    public ResponseEntity<ApiResponse<CitaResponse>> marcarNoAsistio(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.exito("Inasistencia registrada",
                citaService.marcarNoAsistio(id, usuarioAutenticado())));
    }

    private Long usuarioAutenticado() {
        return SecurityUtils.obtenerUsuarioActual()
                .orElseThrow(() -> new IllegalStateException(
                        "Ruta autenticada sin usuario en el contexto"));
    }
}
