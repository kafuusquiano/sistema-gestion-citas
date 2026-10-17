package com.proyecto.gestioncitas.medico.domain.entity;

import com.proyecto.gestioncitas.common.audit.AuditableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.Objects;

/**
 * Especialidad medica: medicina general, obstetricia, enfermeria,
 * odontologia y las demas del alcance del proyecto.
 *
 * Mapea la tabla especialidades de V3__medico.sql.
 */
@Entity
@Table(name = "especialidades")
public class Especialidad extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100, unique = true)
    private String nombre;

    @Column(length = 300)
    private String descripcion;

    @Column(nullable = false)
    private boolean activo = true;

    protected Especialidad() {
        // Requerido por JPA.
    }

    public Especialidad(String nombre, String descripcion) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.activo = true;
    }

    public void actualizar(String nombre, String descripcion) {
        this.nombre = nombre;
        this.descripcion = descripcion;
    }

    /**
     * Desactivar no borra: hay medicos y citas historicas apuntando a esta
     * fila, y la FK es ON DELETE RESTRICT. Una especialidad inactiva
     * desaparece de los selectores sin romper el historial.
     */
    public void desactivar() {
        this.activo = false;
    }

    public void activar() {
        this.activo = true;
    }

    public Long getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public boolean isActivo() {
        return activo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Especialidad otra)) {
            return false;
        }
        return id != null && id.equals(otra.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
