package com.proyecto.gestioncitas.medico.domain.entity;

import com.proyecto.gestioncitas.common.audit.AuditableEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "especialidades")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Especialidad extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false, unique = true)
    private String nombre;

    @Column
    private String descripcion;

    @Column(nullable = false)
    @Builder.Default
    private boolean activa = true;
}
