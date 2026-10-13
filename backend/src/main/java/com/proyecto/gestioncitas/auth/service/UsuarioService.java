package com.proyecto.gestioncitas.auth.service;

import com.proyecto.gestioncitas.auth.domain.entity.Usuario;
import com.proyecto.gestioncitas.auth.domain.enums.EstadoUsuario;
import com.proyecto.gestioncitas.auth.domain.enums.Rol;
import com.proyecto.gestioncitas.common.dto.PageResponse;
import com.proyecto.gestioncitas.auth.domain.dto.response.UsuarioResponse;
import org.springframework.data.domain.Pageable;

/**
 * Gestion de cuentas, reservada al ADMIN segun la matriz de la seccion 2.4.
 */
public interface UsuarioService {

    PageResponse<UsuarioResponse> buscar(Rol rol, EstadoUsuario estado, String texto, Pageable pageable);

    UsuarioResponse obtener(Long id);

    /**
     * Cambia el estado de la cuenta. Al pasar a INACTIVO o BLOQUEADO cierra
     * todas las sesiones abiertas del usuario.
     */
    UsuarioResponse cambiarEstado(Long id, EstadoUsuario nuevoEstado);

    /**
     * Crea una cuenta para un medico o un administrador. La usa AuthFacade,
     * porque el modulo medico no puede instanciar un Usuario por su cuenta.
     */
    Usuario crearCuenta(String email, String dni, String passwordEnClaro, Rol rol);

    /** Para uso interno del modulo; no se expone por el controller. */
    Usuario obtenerEntidad(Long id);
}
