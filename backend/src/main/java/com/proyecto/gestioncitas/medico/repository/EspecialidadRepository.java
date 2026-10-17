package com.proyecto.gestioncitas.medico.repository;

import com.proyecto.gestioncitas.medico.domain.entity.Especialidad;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EspecialidadRepository extends JpaRepository<Especialidad, Long> {

    Optional<Especialidad> findByNombreIgnoreCase(String nombre);

    boolean existsByNombreIgnoreCase(String nombre);

    List<Especialidad> findByActivoTrueOrderByNombreAsc();

    List<Especialidad> findAllByOrderByNombreAsc();
}
