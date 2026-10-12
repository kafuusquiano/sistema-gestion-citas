package com.proyecto.gestioncitas.medico.repository;

import com.proyecto.gestioncitas.medico.domain.entity.HorarioMedico;
import com.proyecto.gestioncitas.medico.domain.enums.DiaSemana;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface HorarioMedicoRepository extends JpaRepository<HorarioMedico, String> {
    List<HorarioMedico> findByMedicoIdAndActivoTrue(String medicoId);
    Optional<HorarioMedico> findByMedicoIdAndDiaSemana(String medicoId, DiaSemana diaSemana);
    boolean existsByMedicoIdAndDiaSemana(String medicoId, DiaSemana diaSemana);
}
