package com.proyecto.gestioncitas.paciente.domain.entity;

import com.proyecto.gestioncitas.common.audit.AuditableEntity;
import com.proyecto.gestioncitas.paciente.domain.enums.Sexo;
import com.proyecto.gestioncitas.paciente.domain.enums.TipoSangre;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "pacientes")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Paciente extends AuditableEntity {

    @Id
    private String id; // mismo ID que Usuario

    @Column(name = "usuario_id", nullable = false, unique = true)
    private String usuarioId;

    @Column(nullable = false, length = 8, unique = true)
    private String dni;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private String apellido;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(name = "fecha_nacimiento")
    private LocalDate fechaNacimiento;

    @Enumerated(EnumType.STRING)
    private Sexo sexo;

    @Column(name = "telefono", length = 15)
    private String telefono;

    @Column(name = "direccion")
    private String direccion;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_sangre")
    private TipoSangre tipoSangre;

    @Column(name = "alergias", columnDefinition = "TEXT")
    private String alergias;

    @Column(name = "activo", nullable = false)
    @Builder.Default
    private boolean activo = true;
}
