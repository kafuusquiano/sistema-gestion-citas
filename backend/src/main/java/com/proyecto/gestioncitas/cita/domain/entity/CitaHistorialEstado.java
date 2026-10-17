package com.proyecto.gestioncitas.cita.domain.entity;

import com.proyecto.gestioncitas.cita.domain.enums.EstadoCita;
import com.proyecto.gestioncitas.common.audit.AuditableEntity;
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
import java.time.Instant;
import java.util.Objects;

/**
 * Registro inmutable de un cambio en una cita.
 *
 * Da la trazabilidad de reprogramaciones e inasistencias que pide el objetivo
 * especifico de seguimiento. Guarda la fecha anterior porque al reprogramar se
 * sobrescribe fecha_hora en la fila de la cita, y sin esto ese dato se perderia.
 *
 * No tiene metodos de modificacion: una vez escrito, un asiento del historial
 * no se corrige.
 */
@Entity
@Table(name = "citas_historial_estado")
public class CitaHistorialEstado extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cita_id", nullable = false)
    private Cita cita;

    /** Nulo en el asiento de creacion de la cita. */
    @Enumerated(EnumType.STRING)
    @Column(name = "estado_anterior", length = 20)
    private EstadoCita estadoAnterior;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_nuevo", nullable = false, length = 20)
    private EstadoCita estadoNuevo;

    /** Solo se llenan en una reprogramacion. */
    @Column(name = "fecha_hora_anterior")
    private Instant fechaHoraAnterior;

    @Column(name = "fecha_hora_nueva")
    private Instant fechaHoraNueva;

    @Column(length = 300)
    private String motivo;

    /** Nulo si el cambio lo hizo un proceso automatico, como el scheduler. */
    @Column(name = "usuario_id")
    private Long usuarioId;

    protected CitaHistorialEstado() {
        // Requerido por JPA.
    }

    CitaHistorialEstado(EstadoCita estadoAnterior, EstadoCita estadoNuevo,
            Instant fechaHoraAnterior, Instant fechaHoraNueva, String motivo, Long usuarioId) {
        this.estadoAnterior = estadoAnterior;
        this.estadoNuevo = estadoNuevo;
        this.fechaHoraAnterior = fechaHoraAnterior;
        this.fechaHoraNueva = fechaHoraNueva;
        this.motivo = motivo;
        this.usuarioId = usuarioId;
    }

    /** Lo llama Cita para mantener los dos lados de la relacion. */
    void asignarCita(Cita cita) {
        this.cita = cita;
    }

    /** true si este asiento corresponde a un cambio de fecha. */
    public boolean esReprogramacion() {
        return fechaHoraAnterior != null && fechaHoraNueva != null;
    }

    public Long getId() {
        return id;
    }

    public Cita getCita() {
        return cita;
    }

    public EstadoCita getEstadoAnterior() {
        return estadoAnterior;
    }

    public EstadoCita getEstadoNuevo() {
        return estadoNuevo;
    }

    public Instant getFechaHoraAnterior() {
        return fechaHoraAnterior;
    }

    public Instant getFechaHoraNueva() {
        return fechaHoraNueva;
    }

    public String getMotivo() {
        return motivo;
    }

    public Long getUsuarioId() {
        return usuarioId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CitaHistorialEstado otro)) {
            return false;
        }
        return id != null && id.equals(otro.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
