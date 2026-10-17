package com.proyecto.gestioncitas.notificacion.domain.entity;

import com.proyecto.gestioncitas.common.audit.AuditableEntity;
import com.proyecto.gestioncitas.notificacion.domain.enums.CanalNotificacion;
import com.proyecto.gestioncitas.notificacion.domain.enums.EstadoNotificacion;
import com.proyecto.gestioncitas.notificacion.domain.enums.TipoNotificacion;
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
 * Aviso dirigido a un usuario. Mapea la tabla notificaciones de
 * V5__notificacion.sql.
 *
 * Guarda destinatarioId como Long y no una relacion a Usuario, que pertenece
 * al modulo auth (instruccion M1).
 *
 * referenciaTipo y referenciaId dicen a donde navegar al tocar el aviso. No
 * son clave ajena porque pueden apuntar a recursos de distinta naturaleza.
 */
@Entity
@Table(name = "notificaciones")
public class Notificacion extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "destinatario_id", nullable = false, updatable = false)
    private Long destinatarioId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private TipoNotificacion tipo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private CanalNotificacion canal;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoNotificacion estado = EstadoNotificacion.PENDIENTE;

    @Column(nullable = false, length = 200)
    private String asunto;

    /**
     * Cuerpo del correo en HTML. Se mapea con columnDefinition = "TEXT" y NO
     * con @Lob: sobre un String, @Lob hace que Hibernate espere el tipo oid de
     * PostgreSQL, que es almacenamiento de objetos grandes, mientras que la
     * columna es TEXT. La validacion de esquema detiene el arranque con
     * "found [text], but expecting [oid]".
     */
    @Column(nullable = false, columnDefinition = "TEXT")
    private String mensaje;

    @Column(name = "referencia_tipo", length = 30)
    private String referenciaTipo;

    @Column(name = "referencia_id")
    private Long referenciaId;

    @Column(name = "enviado_en")
    private Instant enviadoEn;

    @Column(name = "leido_en")
    private Instant leidoEn;

    @Column(nullable = false)
    private short intentos = 0;

    @Column(name = "ultimo_error", length = 500)
    private String ultimoError;

    /**
     * Correo de destino. No se persiste: se resuelve al enviar, porque el
     * correo vive en el modulo auth y duplicarlo aqui lo dejaria desfasado
     * cuando el usuario lo cambie.
     */
    @jakarta.persistence.Transient
    private String correoDestino;

    protected Notificacion() {
        // Requerido por JPA.
    }

    public Notificacion(Long destinatarioId, TipoNotificacion tipo, CanalNotificacion canal,
            String asunto, String mensaje) {
        this.destinatarioId = destinatarioId;
        this.tipo = tipo;
        this.canal = canal;
        this.asunto = asunto;
        this.mensaje = mensaje;
        this.estado = EstadoNotificacion.PENDIENTE;
    }

    /** Enlaza el aviso con la cita que lo origino, para la navegacion. */
    public void referirA(String tipoRecurso, Long idRecurso) {
        this.referenciaTipo = tipoRecurso;
        this.referenciaId = idRecurso;
    }

    public void marcarEnviada() {
        this.estado = EstadoNotificacion.ENVIADA;
        this.enviadoEn = Instant.now();
        this.ultimoError = null;
    }

    public void marcarLeida() {
        // Solo tiene sentido marcar como leido algo que llego. Si estaba
        // PENDIENTE, leerla implica que se entrego.
        if (this.enviadoEn == null) {
            this.enviadoEn = Instant.now();
        }
        this.estado = EstadoNotificacion.LEIDA;
        this.leidoEn = Instant.now();
    }

    /** Suma un intento y guarda el error, recortado al largo de la columna. */
    public void marcarFallida(String error) {
        this.estado = EstadoNotificacion.FALLIDA;
        this.intentos++;
        this.ultimoError = error == null ? null
                : error.substring(0, Math.min(error.length(), 500));
    }

    public boolean admiteReintento(int maximoIntentos) {
        return estado.admiteReintento() && intentos < maximoIntentos;
    }

    public boolean estaLeida() {
        return estado == EstadoNotificacion.LEIDA;
    }

    public void asignarCorreoDestino(String correo) {
        this.correoDestino = correo;
    }

    public String getCorreoDestino() {
        return correoDestino;
    }

    // ---------- Accesores ----------

    public Long getId() {
        return id;
    }

    public Long getDestinatarioId() {
        return destinatarioId;
    }

    public TipoNotificacion getTipo() {
        return tipo;
    }

    public CanalNotificacion getCanal() {
        return canal;
    }

    public EstadoNotificacion getEstado() {
        return estado;
    }

    public String getAsunto() {
        return asunto;
    }

    public String getMensaje() {
        return mensaje;
    }

    public String getReferenciaTipo() {
        return referenciaTipo;
    }

    public Long getReferenciaId() {
        return referenciaId;
    }

    public Instant getEnviadoEn() {
        return enviadoEn;
    }

    public Instant getLeidoEn() {
        return leidoEn;
    }

    public short getIntentos() {
        return intentos;
    }

    public String getUltimoError() {
        return ultimoError;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Notificacion otra)) {
            return false;
        }
        return id != null && id.equals(otra.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
