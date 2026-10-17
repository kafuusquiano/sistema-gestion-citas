package com.proyecto.gestioncitas.medico.repository;

import com.proyecto.gestioncitas.medico.domain.entity.ExcepcionHorario;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ExcepcionHorarioRepository extends JpaRepository<ExcepcionHorario, Long> {

    /**
     * Excepciones que cubren una fecha para un medico dado.
     *
     * Incluye las del medico y las generales del establecimiento, que tienen
     * medico nulo. Es la consulta que resta dias no laborables al calcular la
     * disponibilidad.
     */
    @Query("""
            SELECT e FROM ExcepcionHorario e
            WHERE (e.medico IS NULL OR e.medico.id = :medicoId)
              AND :fecha BETWEEN e.fechaInicio AND e.fechaFin
            """)
    List<ExcepcionHorario> listarQueCubren(@Param("medicoId") Long medicoId,
            @Param("fecha") LocalDate fecha);

    /** Para pintar el calendario de un rango completo en una sola consulta. */
    @Query("""
            SELECT e FROM ExcepcionHorario e
            WHERE (e.medico IS NULL OR e.medico.id = :medicoId)
              AND e.fechaInicio <= :hasta
              AND e.fechaFin    >= :desde
            ORDER BY e.fechaInicio
            """)
    List<ExcepcionHorario> listarEnRango(@Param("medicoId") Long medicoId,
            @Param("desde") LocalDate desde,
            @Param("hasta") LocalDate hasta);

    List<ExcepcionHorario> findByMedicoIdOrderByFechaInicioDesc(Long medicoId);

    /** Feriados y cierres que aplican a todo el establecimiento. */
    List<ExcepcionHorario> findByMedicoIsNullOrderByFechaInicioDesc();
}
