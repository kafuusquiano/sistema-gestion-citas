package com.proyecto.gestioncitas.medico.repository;

import com.proyecto.gestioncitas.medico.domain.entity.Medico;
import com.proyecto.gestioncitas.medico.domain.enums.EstadoMedico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import java.util.List;
import java.util.Optional;

public interface MedicoRepository extends JpaRepository<Medico, String>,
        JpaSpecificationExecutor<Medico> {
    Optional<Medico> findByUsuarioId(String usuarioId);
    List<Medico> findByEspecialidadIdAndEstado(String especialidadId, EstadoMedico estado);
    boolean existsByDni(String dni);
    boolean existsByEmail(String email);
    boolean existsByCodigoCmp(String codigoCmp);
}
