package com.proyecto.gestioncitas.paciente.repository.spec;

import com.proyecto.gestioncitas.common.util.DateUtils;
import com.proyecto.gestioncitas.paciente.domain.dto.request.PacienteFiltroRequest;
import com.proyecto.gestioncitas.paciente.domain.entity.Paciente;
import jakarta.persistence.criteria.Predicate;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.springframework.data.jpa.domain.Specification;

/**
 * Traduce PacienteFiltroRequest a predicados JPA.
 *
 * Un filtro nulo no agrega predicado, asi que una sola consulta cubre todas
 * las combinaciones sin escribir un metodo por cada una.
 */
public final class PacienteSpecification {

    private PacienteSpecification() {
    }

    public static Specification<Paciente> con(PacienteFiltroRequest filtro) {
        return (raiz, consulta, cb) -> {
            List<Predicate> predicados = new ArrayList<>();

            if (filtro.texto() != null && !filtro.texto().isBlank()) {
                String patron = "%" + filtro.texto().trim().toLowerCase() + "%";
                // Una sola caja de busqueda que cruza documento, nombres,
                // apellidos y numero de historia (objetivo especifico 1).
                predicados.add(cb.or(
                        cb.like(cb.lower(raiz.get("numeroDocumento")), patron),
                        cb.like(cb.lower(raiz.get("nombres")), patron),
                        cb.like(cb.lower(raiz.get("apellidoPaterno")), patron),
                        cb.like(cb.lower(raiz.get("apellidoMaterno")), patron),
                        cb.like(cb.lower(raiz.get("numeroHistoria")), patron)));
            }

            if (filtro.estado() != null) {
                predicados.add(cb.equal(raiz.get("estado"), filtro.estado()));
            }

            if (filtro.sexo() != null) {
                predicados.add(cb.equal(raiz.get("sexo"), filtro.sexo()));
            }

            if (filtro.seguro() != null) {
                predicados.add(cb.equal(raiz.get("seguro"), filtro.seguro()));
            }

            if (filtro.distrito() != null && !filtro.distrito().isBlank()) {
                predicados.add(cb.equal(cb.lower(raiz.get("distrito")),
                        filtro.distrito().trim().toLowerCase()));
            }

            // La edad no es una columna: se filtra invirtiendo el rango sobre
            // fecha_nacimiento. Asi el indice de esa columna sigue sirviendo,
            // que no ocurriria si se calculara la edad en SQL por cada fila.
            LocalDate hoy = DateUtils.hoy();
            if (filtro.edadMinima() != null) {
                predicados.add(cb.lessThanOrEqualTo(raiz.get("fechaNacimiento"),
                        hoy.minusYears(filtro.edadMinima())));
            }
            if (filtro.edadMaxima() != null) {
                predicados.add(cb.greaterThan(raiz.get("fechaNacimiento"),
                        hoy.minusYears(filtro.edadMaxima() + 1L)));
            }

            if (filtro.conCuenta() != null) {
                predicados.add(filtro.conCuenta()
                        ? cb.isNotNull(raiz.get("usuarioId"))
                        : cb.isNull(raiz.get("usuarioId")));
            }

            return predicados.isEmpty()
                    ? cb.conjunction()
                    : cb.and(predicados.toArray(new Predicate[0]));
        };
    }
}
