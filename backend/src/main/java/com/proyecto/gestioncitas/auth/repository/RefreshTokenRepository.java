package com.proyecto.gestioncitas.auth.repository;

import com.proyecto.gestioncitas.auth.domain.entity.RefreshToken;
import java.time.Instant;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByJti(String jti);

    /**
     * Cierra todas las sesiones de un usuario. Lo usa el administrador al
     * bloquear una cuenta: sin esto, el usuario bloqueado seguiria renovando
     * su token hasta que venciera el refresh.
     */
    @Modifying
    @Query("""
            UPDATE RefreshToken rt
               SET rt.revocado = true, rt.revocadoEn = :ahora
             WHERE rt.usuario.id = :usuarioId AND rt.revocado = false
            """)
    int revocarTodosDelUsuario(@Param("usuarioId") Long usuarioId, @Param("ahora") Instant ahora);

    /** Barrido de tokens vencidos, para que la tabla no crezca sin limite. */
    @Modifying
    @Query("DELETE FROM RefreshToken rt WHERE rt.expiraEn < :limite")
    int eliminarVencidos(@Param("limite") Instant limite);

    long countByUsuarioIdAndRevocadoFalse(Long usuarioId);
}
