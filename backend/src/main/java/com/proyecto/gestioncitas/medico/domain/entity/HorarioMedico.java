package com.proyecto.gestioncitas.medico.domain.entity;

import com.proyecto.gestioncitas.common.audit.AuditableEntity;
import com.proyecto.gestioncitas.medico.domain.enums.DiaSemana;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalTime;

@Entity
@Table(name = "horarios_medico",
    uniqueConstraints = @UniqueConstraint(columnNames = {"medico_id","dia_semana"}))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class HorarioMedico extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "medico_id", nullable = false)
    private Medico medico;

    @Enumerated(EnumType.STRING)
    @Column(name = "dia_semana", nullable = false)
    private DiaSemana diaSemana;

    @Column(name = "hora_inicio", nullable = false)
    private LocalTime horaInicio;

    @Column(name = "hora_fin", nullable = false)
    private LocalTime horaFin;

    @Column(name = "duracion_cita_min", nullable = false)
    @Builder.Default
    private int duracionCitaMin = 30;

    @Column(nullable = false)
    @Builder.Default
    private boolean activo = true;
}
