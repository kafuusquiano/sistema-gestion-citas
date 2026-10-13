package com.proyecto.gestioncitas.auth.controller;

import com.proyecto.gestioncitas.auth.domain.dto.response.UsuarioResponse;
import com.proyecto.gestioncitas.auth.domain.enums.EstadoUsuario;
import com.proyecto.gestioncitas.auth.domain.enums.Rol;
import com.proyecto.gestioncitas.auth.service.UsuarioService;
import com.proyecto.gestioncitas.common.constants.AppConstants;
import com.proyecto.gestioncitas.common.dto.ApiResponse;
import com.proyecto.gestioncitas.common.dto.PageResponse;
import com.proyecto.gestioncitas.common.util.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Gestion de cuentas. Reservado al ADMIN segun la matriz de la seccion 2.4,
 * con una excepcion: /me, que cualquiera puede consultar sobre si mismo.
 */
@RestController
@RequestMapping("/api/usuarios")
@Tag(name = "Usuarios", description = "Administracion de cuentas de acceso")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Listar cuentas",
            description = "Filtros opcionales por rol, estado y texto libre sobre correo o DNI.")
    public ResponseEntity<ApiResponse<PageResponse<UsuarioResponse>>> listar(
            @RequestParam(required = false) Rol rol,
            @RequestParam(required = false) EstadoUsuario estado,
            @RequestParam(required = false) String texto,
            @RequestParam(defaultValue = "0") @Min(0) int pagina,
            @RequestParam(defaultValue = "20") @Min(1) @Max(AppConstants.TAMANO_PAGINA_MAXIMO) int tamano) {

        var pageable = PageRequest.of(pagina, tamano, Sort.by(Sort.Direction.DESC, "createdAt"));
        var resultado = usuarioService.buscar(rol, estado, texto, pageable);
        return ResponseEntity.ok(ApiResponse.exito(resultado));
    }

    @GetMapping("/me")
    @Operation(summary = "Ver la cuenta propia",
            description = "Devuelve la cuenta del usuario autenticado, sea cual sea su rol.")
    public ResponseEntity<ApiResponse<UsuarioResponse>> verMiCuenta() {
        Long id = SecurityUtils.obtenerUsuarioActual()
                .orElseThrow(() -> new IllegalStateException(
                        "Ruta autenticada sin usuario en el contexto"));
        return ResponseEntity.ok(ApiResponse.exito(usuarioService.obtener(id)));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Ver una cuenta por id")
    public ResponseEntity<ApiResponse<UsuarioResponse>> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.exito(usuarioService.obtener(id)));
    }

    @PatchMapping("/{id}/estado")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Cambiar el estado de una cuenta",
            description = """
                    Al pasar a INACTIVO o BLOQUEADO se cierran todas las sesiones
                    abiertas del usuario. Sin eso conservaria acceso hasta que
                    venciera su refresh token.
                    """)
    public ResponseEntity<ApiResponse<UsuarioResponse>> cambiarEstado(
            @PathVariable Long id,
            @RequestParam @NotNull EstadoUsuario estado) {

        var actualizado = usuarioService.cambiarEstado(id, estado);
        return ResponseEntity.ok(ApiResponse.exito("Estado actualizado", actualizado));
    }
}
