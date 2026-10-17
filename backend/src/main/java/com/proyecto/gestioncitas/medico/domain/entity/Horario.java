package com.proyecto.gestioncitas.medico.domain.entity;

import com.proyecto.gestioncitas.common.audit.AuditableEntity;
import com.proyecto.gestioncitas.medico.domain.enums.DiaSemana;
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
import java.time.LocalTime;
import java.util.Objects;

/**
 * Franja de atencion semanal de un medico. Mapea horarios de V3__medico.sql.
 *
 * Es una plantilla, no una agenda: dice "los lunes de 08:00 a 12:00, en
 * turnos de 20 minutos". DisponibilidadService la proyecta sobre una fecha
 * concreta, resta las excepciones y las citas ya tomadas, y de ahi salen los
 * slots libres.
 */
@Entity
@Table(name = "horarios")
public class Horario extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "medico_id", nullable = false)
    private Medico medico;

    @Enumerated(EnumType.STRING)
    @Column(name = "dia_semana", nullable = false, length = 10)
    private DiaSemana diaSemana;

    @Column(name = "hora_inicio", nullable = false)
    private LocalTime horaInicio;

    @Column(name = "hora_fin", nullable = false)
    private LocalTime horaFin;

    /** Minutos de cada turno. El CHECK admite 10, 15, 20, 30, 45 o 60. */
    @Column(name = "duracion_slot_minutos", nullable = false)
    private short duracionSlotMinutos = 20;

    @Column(nullable = false)
    private boolean activo = true;

    protected Horario() {
        // Requerido por JPA.
    }

    public Horario(DiaSemana diaSemana, LocalTime horaInicio, LocalTime horaFin,
            short duracionSlotMinutos) {
        if (!horaFin.isAfter(horaInicio)) {
            throw new IllegalArgumentException("La hora de fin debe ser posterior a la de inicio");
        }
        this.diaSemana = diaSemana;
        this.horaInicio = horaInicio;
        this.horaFin = horaFin;
        this.duracionSlotMinutos = duracionSlotMinutos;
        this.activo = true;
    }

    /** Lo llama Medico para mantener los dos lados de la relacion. */
    void asignarMedico(Medico medico) {
        this.medico = medico;
    }

    public void desactivar() {
        this.activo = false;
    }

    public void activar() {
        this.activo = true;
    }

    /**
     * Cuantos turnos caben en la franja.
     *
     * Division entera a proposito: si la franja no es multiplo exacto de la
     * duracion, el resto no alcanza para una consulta completa y se descarta.
     */
    public int cantidadSlots() {
        long minutos = java.time.Duration.between(horaInicio, horaFin).toMinutes();
        return (int) (minutos / duracionSlotMinutos);
    }

    /** true si la hora cae dentro de la franja y coincide con el inicio de un turno. */
    public boolean contieneSlot(LocalTime hora) {
        if (hora.isBefore(horaInicio) || !hora.isBefore(horaFin)) {
            return false;
        }
        long desdeInicio = java.time.Duration.between(horaInicio, hora).toMinutes();
        return desdeInicio % duracionSlotMinutos == 0;
    }

    /** true si esta franja se cruza con la otra en el mismo dia. */
    public boolean seCruzaCon(Horario otro) {
        if (this.diaSemana != otro.diaSemana) {
            return false;
        }
        // Dos franjas se cruzan si cada una empieza antes de que la otra acabe.
        return this.horaInicio.isBefore(otro.horaFin) && otro.horaInicio.isBefore(this.horaFin);
    }

    public Long getId() {
        return id;
    }

    public Medico getMedico() {
        return medico;
    }

    public DiaSemana getDiaSemana() {
        return diaSemana;
    }

    public LocalTime getHoraInicio() {
        return horaInicio;
    }

    public LocalTime getHoraFin() {
        return horaFin;
    }

    public short getDuracionSlotMinutos() {
        return duracionSlotMinutos;
    }

    public boolean isActivo() {
        return activo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Horario otro)) {
            return false;
        }
        return id != null && id.equals(otro.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
