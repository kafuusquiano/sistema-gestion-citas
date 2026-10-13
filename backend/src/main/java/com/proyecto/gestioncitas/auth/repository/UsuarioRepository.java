package com.proyecto.gestioncitas.auth.repository;

import com.proyecto.gestioncitas.auth.domain.entity.Usuario;
import com.proyecto.gestioncitas.auth.domain.enums.EstadoUsuario;
import com.proyecto.gestioncitas.auth.domain.enums.Rol;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Acceso a usuarios. Es interno del modulo auth: ningun otro modulo lo
 * importa, usan AuthFacade (instruccion M1).
 */
@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByEmail(String email);

    Optional<Usuario> findByDni(String dni);

    boolean existsByEmail(String email);

    boolean existsByDni(String dni);

    /**
     * Listado del panel de administracion, con filtros opcionales. Un
     * parametro nulo se ignora, asi no hacen falta cuatro metodos distintos.
     */
    @Query("""
            SELECT u FROM Usuario u
            WHERE (:rol    IS NULL OR u.rol    = :rol)
              AND (:estado IS NULL OR u.estado = :estado)
              AND (:texto  IS NULL OR LOWER(u.email) LIKE LOWER(CONCAT('%', :texto, '%'))
                                   OR u.dni LIKE CONCAT('%', :texto, '%'))
            """)
    Page<Usuario> buscar(@Param("rol") Rol rol,
            @Param("estado") EstadoUsuario estado,
            @Param("texto") String texto,
            Pageable pageable);

    long countByRolAndEstado(Rol rol, EstadoUsuario estado);
}
