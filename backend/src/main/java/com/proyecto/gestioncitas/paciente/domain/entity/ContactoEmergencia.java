package com.proyecto.gestioncitas.paciente.domain.entity;

import com.proyecto.gestioncitas.common.audit.AuditableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.util.Objects;

/**
 * A quien avisar en una emergencia. Mapea contactos_emergencia de
 * V2__paciente.sql.
 */
@Entity
@Table(name = "contactos_emergencia")
public class ContactoEmergencia extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "paciente_id", nullable = false)
    private Paciente paciente;

    @Column(nullable = false, length = 150)
    private String nombres;

    @Column(nullable = false, length = 40)
    private String parentesco;

    @Column(nullable = false, length = 20)
    private String telefono;

    /**
     * A quien llamar primero. El indice parcial uk_contactos_principal admite
     * uno solo por paciente, asi que Paciente.agregarContacto desmarca el
     * anterior antes de marcar uno nuevo.
     */
    @Column(name = "es_principal", nullable = false)
    private boolean esPrincipal = false;

    protected ContactoEmergencia() {
        // Requerido por JPA.
    }

    public ContactoEmergencia(String nombres, String parentesco, String telefono, boolean esPrincipal) {
        this.nombres = nombres;
        this.parentesco = parentesco;
        this.telefono = telefono;
        this.esPrincipal = esPrincipal;
    }

    /** Lo llama Paciente para mantener los dos lados de la relacion. */
    void asignarPaciente(Paciente paciente) {
        this.paciente = paciente;
    }

    void quitarPrincipal() {
        this.esPrincipal = false;
    }

    public Long getId() {
        return id;
    }

    public Paciente getPaciente() {
        return paciente;
    }

    public String getNombres() {
        return nombres;
    }

    public String getParentesco() {
        return parentesco;
    }

    public String getTelefono() {
        return telefono;
    }

    public boolean isEsPrincipal() {
        return esPrincipal;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ContactoEmergencia otro)) {
            return false;
        }
        return id != null && id.equals(otro.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
