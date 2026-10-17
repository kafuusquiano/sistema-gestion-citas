package com.proyecto.gestioncitas.cita.repository;

import com.proyecto.gestioncitas.cita.domain.entity.Cita;
import com.proyecto.gestioncitas.cita.domain.enums.EstadoCita;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Acceso a citas. Interno del modulo: los demas usan CitaFacade.
 */
@Repository
public interface CitaRepository extends JpaRepository<Cita, Long> {

    @EntityGraph(attributePaths = "historial")
    Optional<Cita> findWithHistorialById(Long id);

    /**
     * Citas vigentes de un medico en un rango. Es la consulta que alimenta el
     * calculo de disponibilidad: lo que devuelve son los turnos ya tomados.
     *
     * Solo considera PROGRAMADA y CONFIRMADA, igual que el indice unico
     * parcial uk_citas_medico_slot, de modo que una cita cancelada vuelve a
     * ofrecer su cupo.
     */
    @Query("""
            SELECT c FROM Cita c
            WHERE c.medicoId = :medicoId
              AND c.fechaHora >= :desde
              AND c.fechaHora <  :hasta
              AND c.estado IN ('PROGRAMADA', 'CONFIRMADA')
            ORDER BY c.fechaHora
            """)
    List<Cita> listarVigentesDelMedicoEnRango(@Param("medicoId") Long medicoId,
            @Param("desde") Instant desde,
            @Param("hasta") Instant hasta);

    /** Comprobacion previa a guardar. El indice unico es la garantia final. */
    @Query("""
            SELECT COUNT(c) > 0 FROM Cita c
            WHERE c.medicoId = :medicoId
              AND c.fechaHora = :fechaHora
              AND c.estado IN ('PROGRAMADA', 'CONFIRMADA')
            """)
    boolean existeCitaVigenteEnSlot(@Param("medicoId") Long medicoId,
            @Param("fechaHora") Instant fechaHora);

    /** Agenda del paciente, mas recientes primero. */
    Page<Cita> findByPacienteIdOrderByFechaHoraDesc(Long pacienteId, Pageable pageable);

    /** Agenda del dia de un medico. */
    @Query("""
            SELECT c FROM Cita c
            WHERE c.medicoId = :medicoId
              AND c.fechaHora >= :desde
              AND c.fechaHora <  :hasta
            ORDER BY c.fechaHora
            """)
    List<Cita> listarAgendaDelMedico(@Param("medicoId") Long medicoId,
            @Param("desde") Instant desde,
            @Param("hasta") Instant hasta);

    /** Listado del panel de administracion, con filtros opcionales. */
    @Query("""
            SELECT c FROM Cita c
            WHERE (:pacienteId IS NULL OR c.pacienteId = :pacienteId)
              AND (:medicoId   IS NULL OR c.medicoId   = :medicoId)
              AND (:estado     IS NULL OR c.estado     = :estado)
              AND (CAST(:desde AS timestamp) IS NULL OR c.fechaHora >= :desde)
              AND (CAST(:hasta AS timestamp) IS NULL OR c.fechaHora <  :hasta)
            ORDER BY c.fechaHora DESC
            """)
    Page<Cita> buscar(@Param("pacienteId") Long pacienteId,
            @Param("medicoId") Long medicoId,
            @Param("estado") EstadoCita estado,
            @Param("desde") Instant desde,
            @Param("hasta") Instant hasta,
            Pageable pageable);

    /**
     * Citas vigentes cuya hora ya paso, para marcarlas como inasistencia.
     *
     * Es la consulta del proceso automatico, y por eso existe el indice
     * parcial idx_citas_pendientes: recorre solo las vigentes, no la tabla.
     */
    @Query("""
            SELECT c FROM Cita c
            WHERE c.fechaHora < :limite
              AND c.estado IN ('PROGRAMADA', 'CONFIRMADA')
            """)
    List<Cita> listarVencidasSinAtender(@Param("limite") Instant limite);

    /** Citas que empiezan en una ventana, para los recordatorios. */
    @Query("""
            SELECT c FROM Cita c
            WHERE c.fechaHora >= :desde
              AND c.fechaHora <  :hasta
              AND c.estado IN ('PROGRAMADA', 'CONFIRMADA')
            """)
    List<Cita> listarParaRecordatorio(@Param("desde") Instant desde,
            @Param("hasta") Instant hasta);

    long countByMedicoIdAndEstado(Long medicoId, EstadoCita estado);

    long countByPacienteIdAndEstado(Long pacienteId, EstadoCita estado);
}
