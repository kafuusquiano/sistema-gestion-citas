package com.proyecto.gestioncitas.medico.domain.entity;

import com.proyecto.gestioncitas.common.audit.AuditableEntity;
import com.proyecto.gestioncitas.medico.domain.enums.EstadoMedico;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "medicos")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Medico extends AuditableEntity {

    @Id
    private String id;

    @Column(name = "usuario_id", nullable = false, unique = true)
    private String usuarioId;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private String apellido;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false, unique = true, length = 8)
    private String dni;

    @Column(name = "codigo_cmp", unique = true)
    private String codigoCmp;

    @Column(name = "telefono", length = 15)
    private String telefono;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "especialidad_id")
    private Especialidad especialidad;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private EstadoMedico estado = EstadoMedico.ACTIVO;
}
