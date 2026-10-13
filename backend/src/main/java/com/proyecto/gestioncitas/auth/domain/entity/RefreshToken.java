package com.proyecto.gestioncitas.auth.domain.entity;

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
import java.time.Instant;
import java.util.Objects;

/**
 * Una sesion abierta. Mapea la tabla refresh_tokens de V1__auth.sql.
 *
 * Guarda solo el jti y no el token completo: con el jti basta para revocar
 * la sesion, y asi una filtracion de esta tabla no entrega tokens usables.
 *
 * Se persiste en PostgreSQL y no solo en Redis por dos razones: en dev no hay
 * Redis, y la sesion debe sobrevivir al reinicio del backend, que en Render
 * ocurre cada vez que el servicio despierta de la suspension.
 */
@Entity
@Table(name = "refresh_tokens")
public class RefreshToken extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Column(nullable = false, length = 64, unique = true)
    private String jti;

    @Column(name = "expira_en", nullable = false)
    private Instant expiraEn;

    @Column(nullable = false)
    private boolean revocado = false;

    @Column(name = "revocado_en")
    private Instant revocadoEn;

    protected RefreshToken() {
        // Requerido por JPA.
    }

    public RefreshToken(Usuario usuario, String jti, Instant expiraEn) {
        this.usuario = usuario;
        this.jti = jti;
        this.expiraEn = expiraEn;
        this.revocado = false;
    }

    public void revocar() {
        this.revocado = true;
        this.revocadoEn = Instant.now();
    }

    public boolean estaVencido() {
        return Instant.now().isAfter(expiraEn);
    }

    /** Solo un token vigente y sin revocar sirve para renovar la sesion. */
    public boolean esUtilizable() {
        return !revocado && !estaVencido();
    }

    public Long getId() {
        return id;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public String getJti() {
        return jti;
    }

    public Instant getExpiraEn() {
        return expiraEn;
    }

    public boolean isRevocado() {
        return revocado;
    }

    public Instant getRevocadoEn() {
        return revocadoEn;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof RefreshToken otro)) {
            return false;
        }
        return id != null && id.equals(otro.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
