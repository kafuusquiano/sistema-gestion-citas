package com.proyecto.gestioncitas.auth.domain.entity;

import com.proyecto.gestioncitas.auth.domain.enums.EstadoUsuario;
import com.proyecto.gestioncitas.auth.domain.enums.Rol;
import com.proyecto.gestioncitas.common.audit.AuditableEntity;
import com.proyecto.gestioncitas.common.constants.AppConstants;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.Objects;

/**
 * Credenciales y rol. Los datos personales viven en Paciente y en Medico,
 * no aqui: un usuario es solo una forma de entrar al sistema.
 *
 * Mapea la tabla usuarios de V1__auth.sql.
 */
@Entity
@Table(name = "usuarios")
public class Usuario extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150, unique = true)
    private String email;

    @Column(nullable = false, length = 8, unique = true)
    private String dni;

    /** Hash BCrypt. Nunca la contrasena en claro. */
    @Column(name = "password_hash", nullable = false, length = 100)
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Rol rol;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoUsuario estado = EstadoUsuario.ACTIVO;

    @Column(name = "intentos_fallidos", nullable = false)
    private short intentosFallidos = 0;

    @Column(name = "ultimo_acceso")
    private Instant ultimoAcceso;

    protected Usuario() {
        // Requerido por JPA.
    }

    public Usuario(String email, String dni, String passwordHash, Rol rol) {
        this.email = email;
        this.dni = dni;
        this.passwordHash = passwordHash;
        this.rol = rol;
        this.estado = EstadoUsuario.ACTIVO;
    }

    // ---------- Comportamiento ----------
    // La entidad protege sus propias reglas en vez de exponer setters
    // sueltos: es el encapsulamiento de la seccion 1.3.1.

    /**
     * Suma un intento fallido y bloquea la cuenta al alcanzar el maximo.
     *
     * @return true si este intento dejo la cuenta bloqueada
     */
    public boolean registrarIntentoFallido(int maximoIntentos) {
        this.intentosFallidos++;
        if (this.intentosFallidos >= maximoIntentos) {
            this.estado = EstadoUsuario.BLOQUEADO;
            return true;
        }
        return false;
    }

    /** Login correcto: reinicia el contador y anota la fecha. */
    public void registrarAccesoExitoso() {
        this.intentosFallidos = 0;
        this.ultimoAcceso = Instant.now();
    }

    public void cambiarEstado(EstadoUsuario nuevoEstado) {
        this.estado = nuevoEstado;
        // Reactivar una cuenta bloqueada tiene que limpiar el contador, o
        // volveria a bloquearse en el primer error de tipeo.
        if (nuevoEstado == EstadoUsuario.ACTIVO) {
            this.intentosFallidos = 0;
        }
    }

    public void cambiarPassword(String nuevoHash) {
        this.passwordHash = nuevoHash;
    }

    public boolean puedeIniciarSesion() {
        return estado.puedeIniciarSesion();
    }

    /** Lo que se usa como authority en Spring Security. */
    public String authority() {
        return rol.comoAuthority();
    }

    /** Nombre de la zona horaria para formatear fechas en los correos. */
    public String zonaHoraria() {
        return AppConstants.ZONA_HORARIA;
    }

    // ---------- Accesores ----------

    public Long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getDni() {
        return dni;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public Rol getRol() {
        return rol;
    }

    public EstadoUsuario getEstado() {
        return estado;
    }

    public short getIntentosFallidos() {
        return intentosFallidos;
    }

    public Instant getUltimoAcceso() {
        return ultimoAcceso;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Usuario otro)) {
            return false;
        }
        // Dos entidades sin id todavia no son la misma fila.
        return id != null && id.equals(otro.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
