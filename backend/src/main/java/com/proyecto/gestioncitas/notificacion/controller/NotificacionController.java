package com.proyecto.gestioncitas.notificacion.controller;

import com.proyecto.gestioncitas.common.constants.AppConstants;
import com.proyecto.gestioncitas.common.dto.ApiResponse;
import com.proyecto.gestioncitas.common.dto.PageResponse;
import com.proyecto.gestioncitas.common.util.SecurityUtils;
import com.proyecto.gestioncitas.notificacion.domain.dto.response.NotificacionResponse;
import com.proyecto.gestioncitas.notificacion.service.NotificacionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import java.util.Map;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Campana de notificaciones.
 *
 * Todas las rutas operan sobre los avisos del usuario autenticado. No existe
 * forma de consultar los de otra persona, ni siquiera siendo ADMIN: el id del
 * destinatario sale del token y nunca de un parametro.
 */
@RestController
@RequestMapping("/api/notificaciones")
@Tag(name = "Notificaciones", description = "Avisos al usuario")
public class NotificacionController {

    private final NotificacionService notificacionService;

    public NotificacionController(NotificacionService notificacionService) {
        this.notificacionService = notificacionService;
    }

    @GetMapping
    @Operation(summary = "Ver mis notificaciones")
    public ResponseEntity<ApiResponse<PageResponse<NotificacionResponse>>> listar(
            @RequestParam(defaultValue = "0") @Min(0) int pagina,
            @RequestParam(defaultValue = "20") @Min(1) @Max(AppConstants.TAMANO_PAGINA_MAXIMO) int tamano) {

        var pageable = PageRequest.of(pagina, tamano);
        return ResponseEntity.ok(ApiResponse.exito(
                notificacionService.misNotificaciones(usuarioAutenticado(), pageable)));
    }

    @GetMapping("/no-leidas")
    @Operation(summary = "Contar mis notificaciones no leidas",
            description = "Es el numero que muestra el icono de la campana.")
    public ResponseEntity<ApiResponse<Map<String, Long>>> contarNoLeidas() {
        long total = notificacionService.contarNoLeidas(usuarioAutenticado());
        return ResponseEntity.ok(ApiResponse.exito(Map.of("noLeidas", total)));
    }

    @PatchMapping("/{id}/leida")
    @Operation(summary = "Marcar una notificacion como leida",
            description = "Responde 404 si el aviso no existe o no pertenece al usuario autenticado.")
    public ResponseEntity<ApiResponse<NotificacionResponse>> marcarLeida(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.exito("Notificacion marcada como leida",
                notificacionService.marcarLeida(id, usuarioAutenticado())));
    }

    private Long usuarioAutenticado() {
        return SecurityUtils.obtenerUsuarioActual()
                .orElseThrow(() -> new IllegalStateException(
                        "Ruta autenticada sin usuario en el contexto"));
    }
}
