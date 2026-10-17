package com.proyecto.gestioncitas.medico.repository;

import com.proyecto.gestioncitas.medico.domain.entity.Medico;
import com.proyecto.gestioncitas.medico.domain.enums.EstadoMedico;
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
 * Acceso a medicos. Interno del modulo: los demas usan MedicoFacade.
 *
 * El tipo generico es la clase base Medico. Hibernate devuelve la subclase
 * correcta segun el discriminador, asi que una misma consulta trae
 * MedicoGeneral y MedicoEspecialista mezclados y el polimorfismo funciona sin
 * que el repositorio sepa nada de las subclases.
 */
@Repository
public interface MedicoRepository extends JpaRepository<Medico, Long> {

    Optional<Medico> findByUsuarioId(Long usuarioId);

    Optional<Medico> findByCmp(String cmp);

    boolean existsByCmp(String cmp);

    boolean existsByUsuarioId(Long usuarioId);

    /**
     * Carga el medico con su especialidad y sus horarios en una consulta.
     *
     * Sin el EntityGraph, serializar la ficha dispara una consulta por cada
     * relacion perezosa: el problema N+1 clasico.
     */
    @EntityGraph(attributePaths = { "especialidad", "horarios" })
    Optional<Medico> findWithHorariosById(Long id);

    @Query("""
            SELECT m FROM Medico m
            WHERE (:especialidadId IS NULL OR m.especialidad.id = :especialidadId)
              AND (:estado         IS NULL OR m.estado          = :estado)
              AND (:texto          IS NULL
                   OR LOWER(m.nombres)          LIKE LOWER(CONCAT('%', :texto, '%'))
                   OR LOWER(m.apellidoPaterno)  LIKE LOWER(CONCAT('%', :texto, '%'))
                   OR LOWER(m.apellidoMaterno)  LIKE LOWER(CONCAT('%', :texto, '%'))
                   OR m.cmp                     LIKE CONCAT('%', :texto, '%'))
            """)
    Page<Medico> buscar(@Param("especialidadId") Long especialidadId,
            @Param("estado") EstadoMedico estado,
            @Param("texto") String texto,
            Pageable pageable);

    /** Medicos de una especialidad que admiten citas. Para el selector al agendar. */
    @Query("""
            SELECT m FROM Medico m
            JOIN FETCH m.especialidad
            WHERE m.especialidad.id = :especialidadId AND m.estado = 'ACTIVO'
            ORDER BY m.apellidoPaterno, m.nombres
            """)
    List<Medico> listarActivosPorEspecialidad(@Param("especialidadId") Long especialidadId);

    long countByEspecialidadIdAndEstado(Long especialidadId, EstadoMedico estado);
}
