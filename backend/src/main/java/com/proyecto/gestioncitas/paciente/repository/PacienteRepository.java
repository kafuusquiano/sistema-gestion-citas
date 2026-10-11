package com.proyecto.gestioncitas.paciente.repository;

import com.proyecto.gestioncitas.paciente.domain.entity.Paciente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import java.util.Optional;

public interface PacienteRepository extends JpaRepository<Paciente, String>,
        JpaSpecificationExecutor<Paciente> {
    Optional<Paciente> findByUsuarioId(String usuarioId);
    Optional<Paciente> findByDni(String dni);
    Optional<Paciente> findByEmail(String email);
    boolean existsByDni(String dni);
    boolean existsByEmail(String email);
}
