package com.proyecto.gestioncitas.medico.repository;

import com.proyecto.gestioncitas.medico.domain.entity.Horario;
import com.proyecto.gestioncitas.medico.domain.enums.DiaSemana;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HorarioRepository extends JpaRepository<Horario, Long> {

    List<Horario> findByMedicoIdOrderByDiaSemanaAscHoraInicioAsc(Long medicoId);

    /**
     * Franjas activas de un medico en un dia concreto.
     *
     * Es la consulta que usa el calculo de disponibilidad, y por eso existe
     * el indice parcial idx_horarios_medico_dia, limitado a activo = TRUE.
     */
    List<Horario> findByMedicoIdAndDiaSemanaAndActivoTrue(Long medicoId, DiaSemana diaSemana);

    Optional<Horario> findByIdAndMedicoId(Long id, Long medicoId);

    void deleteByMedicoId(Long medicoId);
}
