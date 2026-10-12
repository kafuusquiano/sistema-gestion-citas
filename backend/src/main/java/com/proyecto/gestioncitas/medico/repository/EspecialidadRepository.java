package com.proyecto.gestioncitas.medico.repository;

import com.proyecto.gestioncitas.medico.domain.entity.Especialidad;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface EspecialidadRepository extends JpaRepository<Especialidad, String> {
    List<Especialidad> findByActivaTrue();
    boolean existsByNombre(String nombre);
}
