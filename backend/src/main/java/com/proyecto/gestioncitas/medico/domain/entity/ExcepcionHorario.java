package com.proyecto.gestioncitas.medico.domain.entity;

import com.proyecto.gestioncitas.common.audit.AuditableEntity;
import com.proyecto.gestioncitas.medico.domain.enums.MotivoExcepcion;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.util.Objects;

/**
 * Rango de fechas en que no se atiende. Mapea excepciones_horario de
 * V3__medico.sql.
 *
 * medico nulo significa que la excepcion aplica a todo el establecimiento,
 * como un feriado: asi no hay que repetir la misma fila por cada medico.
 */
@Entity
@Table(name = "excepciones_horario")
public class ExcepcionHorario extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Nulo cuando aplica a todo el establecimiento. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "medico_id")
    private Medico medico;

    @Column(name = "fecha_inicio", nullable = false)
    private LocalDate fechaInicio;

    @Column(name = "fecha_fin", nullable = false)
    private LocalDate fechaFin;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private MotivoExcepcion motivo;

    @Column(length = 200)
    private String descripcion;

    protected ExcepcionHorario() {
        // Requerido por JPA.
    }

    public ExcepcionHorario(Medico medico, LocalDate fechaInicio, LocalDate fechaFin,
            MotivoExcepcion motivo, String descripcion) {
        if (fechaFin.isBefore(fechaInicio)) {
            throw new IllegalArgumentException(
                    "La fecha de fin no puede ser anterior a la de inicio");
        }
        this.medico = medico;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.motivo = motivo;
        this.descripcion = descripcion;
    }

    /** Rango inclusivo en los dos extremos: un dia libre se registra con la misma fecha. */
    public boolean cubre(LocalDate fecha) {
        return !fecha.isBefore(fechaInicio) && !fecha.isAfter(fechaFin);
    }

    /** true si aplica a todos los medicos del establecimiento. */
    public boolean esGeneral() {
        return medico == null;
    }

    public void actualizar(LocalDate fechaInicio, LocalDate fechaFin, MotivoExcepcion motivo,
            String descripcion) {
        if (fechaFin.isBefore(fechaInicio)) {
            throw new IllegalArgumentException(
                    "La fecha de fin no puede ser anterior a la de inicio");
        }
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.motivo = motivo;
        this.descripcion = descripcion;
    }

    public Long getId() {
        return id;
    }

    public Medico getMedico() {
        return medico;
    }

    public LocalDate getFechaInicio() {
        return fechaInicio;
    }

    public LocalDate getFechaFin() {
        return fechaFin;
    }

    public MotivoExcepcion getMotivo() {
        return motivo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ExcepcionHorario otra)) {
            return false;
        }
        return id != null && id.equals(otra.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
