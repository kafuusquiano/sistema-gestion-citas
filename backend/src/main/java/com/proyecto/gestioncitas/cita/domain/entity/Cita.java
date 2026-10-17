package com.proyecto.gestioncitas.cita.domain.entity;

import com.proyecto.gestioncitas.cita.domain.enums.EstadoCita;
import com.proyecto.gestioncitas.cita.domain.enums.TipoCita;
import com.proyecto.gestioncitas.common.audit.AuditableEntity;
import com.proyecto.gestioncitas.common.util.DateUtils;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Cita medica. Mapea la tabla citas de V4__cita.sql.
 *
 * Guarda pacienteId y medicoId como Long y no relaciones @ManyToOne: esas
 * entidades pertenecen a otros modulos y mapearlas aqui romperia la regla M1.
 * Para obtener sus datos se usan PacienteFacade y MedicoFacade.
 *
 * Los cambios de estado se hacen por metodos con nombre de negocio, no con
 * setters. Cada uno deja su rastro en el historial, de modo que no existe
 * forma de cambiar el estado sin registrar el cambio.
 */
@Entity
@Table(name = "citas")
public class Cita extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "paciente_id", nullable = false, updatable = false)
    private Long pacienteId;

    @Column(name = "medico_id", nullable = false, updatable = false)
    private Long medicoId;

    @Column(name = "fecha_hora", nullable = false)
    private Instant fechaHora;

    @Column(name = "duracion_minutos", nullable = false)
    private short duracionMinutos;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoCita estado = EstadoCita.PROGRAMADA;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TipoCita tipo = TipoCita.CONSULTA;

    @Column(length = 300)
    private String motivo;

    @Column(length = 500)
    private String observaciones;

    @Column(name = "motivo_cancelacion", length = 300)
    private String motivoCancelacion;

    @Column(name = "cancelada_por")
    private Long canceladaPor;

    /**
     * Bloqueo optimista. Si dos operadores modifican la misma cita a la vez,
     * el segundo recibe OptimisticLockingFailureException y el manejador
     * global la traduce a 409, en lugar de sobrescribir el primer cambio.
     */
    @Version
    private Long version;

    @OneToMany(mappedBy = "cita", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CitaHistorialEstado> historial = new ArrayList<>();

    protected Cita() {
        // Requerido por JPA.
    }

    public Cita(Long pacienteId, Long medicoId, Instant fechaHora, short duracionMinutos,
            TipoCita tipo, String motivo) {
        this.pacienteId = pacienteId;
        this.medicoId = medicoId;
        this.fechaHora = fechaHora;
        this.duracionMinutos = duracionMinutos;
        this.tipo = tipo;
        this.motivo = motivo;
        this.estado = EstadoCita.PROGRAMADA;
        registrarEnHistorial(null, EstadoCita.PROGRAMADA, null, null, "Cita agendada", null);
    }

    // ---------- Transiciones de estado ----------
    // Ninguna valida por si misma si la transicion es legal: de eso se encarga
    // CitaEstadoValidator antes de llamarlas. Aqui vive el efecto, alli la regla.

    public void confirmar(Long usuarioId) {
        EstadoCita anterior = this.estado;
        this.estado = EstadoCita.CONFIRMADA;
        registrarEnHistorial(anterior, this.estado, null, null, "Cita confirmada", usuarioId);
    }

    public void cancelar(String motivoCancelacion, Long usuarioId) {
        EstadoCita anterior = this.estado;
        this.estado = EstadoCita.CANCELADA;
        this.motivoCancelacion = motivoCancelacion;
        this.canceladaPor = usuarioId;
        registrarEnHistorial(anterior, this.estado, null, null, motivoCancelacion, usuarioId);
    }

    /**
     * Mueve la cita a otra fecha y hora.
     *
     * Actualiza la fila en lugar de crear una nueva, y vuelve a PROGRAMADA
     * porque el paciente debe confirmar el horario nuevo. La fecha anterior se
     * conserva en el historial: sin ese registro se perderia al sobrescribir.
     */
    public void reprogramar(Instant nuevaFechaHora, String motivoCambio, Long usuarioId) {
        Instant anterior = this.fechaHora;
        EstadoCita estadoAnterior = this.estado;

        this.fechaHora = nuevaFechaHora;
        this.estado = EstadoCita.PROGRAMADA;

        registrarEnHistorial(estadoAnterior, EstadoCita.REPROGRAMADA,
                anterior, nuevaFechaHora, motivoCambio, usuarioId);
    }

    public void completar(String observaciones, Long usuarioId) {
        EstadoCita anterior = this.estado;
        this.estado = EstadoCita.COMPLETADA;
        this.observaciones = observaciones;
        registrarEnHistorial(anterior, this.estado, null, null, "Atencion realizada", usuarioId);
    }

    /** La marca el proceso automatico, por eso el usuario puede ser nulo. */
    public void marcarNoAsistio(Long usuarioId) {
        EstadoCita anterior = this.estado;
        this.estado = EstadoCita.NO_ASISTIO;
        registrarEnHistorial(anterior, this.estado, null, null,
                "El paciente no se presento", usuarioId);
    }

    // ---------- Consultas ----------

    public boolean estaVigente() {
        return estado.ocupaCupo();
    }

    public boolean yaPaso() {
        return fechaHora.isBefore(Instant.now());
    }

    /** Hora de fin, para detectar solapamientos y mostrar la agenda. */
    public Instant fechaHoraFin() {
        return fechaHora.plusSeconds(duracionMinutos * 60L);
    }

    /** Horas que faltan para la atencion. Negativo si ya paso. */
    public long horasHastaLaCita() {
        return java.time.Duration.between(Instant.now(), fechaHora).toHours();
    }

    /** Fecha y hora en la zona del establecimiento, para mostrarla y validarla. */
    public LocalDateTime fechaHoraLocal() {
        return DateUtils.aLocal(fechaHora);
    }

    private void registrarEnHistorial(EstadoCita anterior, EstadoCita nuevo,
            Instant fechaAnterior, Instant fechaNueva, String motivo, Long usuarioId) {
        CitaHistorialEstado registro = new CitaHistorialEstado(
                anterior, nuevo, fechaAnterior, fechaNueva, motivo, usuarioId);
        registro.asignarCita(this);
        historial.add(registro);
    }

    // ---------- Accesores ----------

    public Long getId() {
        return id;
    }

    public Long getPacienteId() {
        return pacienteId;
    }

    public Long getMedicoId() {
        return medicoId;
    }

    public Instant getFechaHora() {
        return fechaHora;
    }

    public short getDuracionMinutos() {
        return duracionMinutos;
    }

    public EstadoCita getEstado() {
        return estado;
    }

    public TipoCita getTipo() {
        return tipo;
    }

    public String getMotivo() {
        return motivo;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public String getMotivoCancelacion() {
        return motivoCancelacion;
    }

    public Long getCanceladaPor() {
        return canceladaPor;
    }

    public Long getVersion() {
        return version;
    }

    public List<CitaHistorialEstado> getHistorial() {
        return List.copyOf(historial);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Cita otra)) {
            return false;
        }
        return id != null && id.equals(otra.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
