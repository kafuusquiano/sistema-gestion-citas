package com.proyecto.gestioncitas.notificacion.repository;

import com.proyecto.gestioncitas.notificacion.domain.entity.Notificacion;
import com.proyecto.gestioncitas.notificacion.domain.enums.EstadoNotificacion;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificacionRepository extends JpaRepository<Notificacion, Long> {

    /** Campana de notificaciones, mas recientes primero. */
    Page<Notificacion> findByDestinatarioIdOrderByCreatedAtDesc(Long destinatarioId,
            Pageable pageable);

    Optional<Notificacion> findByIdAndDestinatarioId(Long id, Long destinatarioId);

    /** Contador del icono de la campana. Lo sostiene idx_notificaciones_no_leidas. */
    long countByDestinatarioIdAndEstadoNot(Long destinatarioId, EstadoNotificacion estado);

    /**
     * Avisos que quedaron sin entregar, para el proceso de reintento.
     *
     * El limite de intentos evita insistir indefinidamente con una direccion
     * invalida. Lo recorre el indice parcial idx_notificaciones_pendientes.
     */
    @Query("""
            SELECT n FROM Notificacion n
            WHERE n.estado IN ('PENDIENTE', 'FALLIDA')
              AND n.intentos < :maximoIntentos
            ORDER BY n.createdAt
            """)
    List<Notificacion> listarPendientes(@Param("maximoIntentos") int maximoIntentos,
            Pageable pageable);

    /** Evita duplicar el recordatorio de una misma cita. */
    @Query("""
            SELECT COUNT(n) > 0 FROM Notificacion n
            WHERE n.destinatarioId  = :destinatarioId
              AND n.tipo            = 'RECORDATORIO'
              AND n.referenciaTipo  = 'CITA'
              AND n.referenciaId    = :citaId
            """)
    boolean existeRecordatorioDeCita(@Param("destinatarioId") Long destinatarioId,
            @Param("citaId") Long citaId);

    /** Barrido de avisos leidos y antiguos, para que la tabla no crezca sin fin. */
    @Query("DELETE FROM Notificacion n WHERE n.estado = 'LEIDA' AND n.leidoEn < :limite")
    @org.springframework.data.jpa.repository.Modifying
    int eliminarLeidasAnterioresA(@Param("limite") Instant limite);
}
