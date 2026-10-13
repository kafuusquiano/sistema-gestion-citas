package com.proyecto.gestioncitas.paciente.repository;

import com.proyecto.gestioncitas.paciente.domain.entity.Paciente;
import com.proyecto.gestioncitas.paciente.domain.enums.TipoDocumento;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * Acceso a pacientes. Interno del modulo: los demas usan PacienteFacade.
 *
 * Extiende JpaSpecificationExecutor para los filtros combinados de
 * PacienteSpecification.
 */
@Repository
public interface PacienteRepository extends JpaRepository<Paciente, Long>,
        JpaSpecificationExecutor<Paciente> {

    Optional<Paciente> findByUsuarioId(Long usuarioId);

    Optional<Paciente> findByTipoDocumentoAndNumeroDocumento(TipoDocumento tipo, String numero);

    Optional<Paciente> findByNumeroHistoria(String numeroHistoria);

    boolean existsByTipoDocumentoAndNumeroDocumento(TipoDocumento tipo, String numero);

    boolean existsByUsuarioId(Long usuarioId);

    /**
     * Siguiente valor de la secuencia creada en V7.
     *
     * Se consulta la secuencia y no un MAX(numero_historia)+1: con dos
     * registros simultaneos en admision, el MAX daria el mismo numero a los
     * dos y uno fallaria contra el indice unico.
     */
    @Query(value = "SELECT nextval('seq_numero_historia')", nativeQuery = true)
    Long siguienteNumeroHistoria();
}
