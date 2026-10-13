package com.proyecto.gestioncitas.common.audit;

import com.proyecto.gestioncitas.common.util.SecurityUtils;
import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import java.time.Instant;

/**
 * Campos de auditoria comunes a las entidades JPA.
 *
 * Usa las callbacks de ciclo de vida de JPA en vez de @EnableJpaAuditing con
 * AuditorAware: el usuario sale del token JWT via SecurityUtils, asi que no
 * hace falta montar la infraestructura de auditoria de Spring Data.
 */
@MappedSuperclass
public abstract class AuditableEntity {

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    /** Id del usuario que creo el registro, o "sistema" si fue un proceso automatico. */
    @Column(name = "created_by", length = 64, updatable = false)
    private String createdBy;

    @Column(name = "updated_by", length = 64)
    private String updatedBy;

    @PrePersist
    protected void alCrear() {
        Instant ahora = Instant.now();
        this.createdAt = ahora;
        this.updatedAt = ahora;
        this.createdBy = SecurityUtils.obtenerUsuarioActualODefecto();
        this.updatedBy = this.createdBy;
    }

    @PreUpdate
    protected void alActualizar() {
        this.updatedAt = Instant.now();
        this.updatedBy = SecurityUtils.obtenerUsuarioActualODefecto();
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }
}
