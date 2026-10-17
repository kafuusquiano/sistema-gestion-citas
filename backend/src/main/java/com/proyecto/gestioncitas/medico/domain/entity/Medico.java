package com.proyecto.gestioncitas.medico.domain.entity;

import com.proyecto.gestioncitas.common.audit.AuditableEntity;
import com.proyecto.gestioncitas.medico.domain.enums.EstadoMedico;
import com.proyecto.gestioncitas.medico.domain.enums.TipoMedico;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.DiscriminatorType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Clase base de la jerarquia de medicos.
 *
 * Es la herencia que declara la seccion 1.3.1 del proyecto: MedicoGeneral y
 * MedicoEspecialista extienden esta clase y comparten lo comun (CMP, nombres,
 * especialidad, horarios), mientras cada una aporta lo propio.
 *
 * Estrategia SINGLE_TABLE: las tres clases viven en la tabla medicos y se
 * distinguen por la columna discriminadora tipo_medico. Se elige esta y no
 * JOINED porque las subclases agregan dos o tres columnas cada una, y partir
 * la tabla obligaria a un JOIN en cada consulta de disponibilidad, que es la
 * mas frecuente del sistema.
 *
 * Los metodos abstractos son el punto de polimorfismo: el codigo que agenda
 * una cita llama a duracionConsultaMinutos() sin preguntar de que subclase se
 * trata, y cada una responde lo suyo.
 */
@Entity
@Table(name = "medicos")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "tipo_medico", discriminatorType = DiscriminatorType.STRING, length = 20)
public abstract class Medico extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Id de la cuenta en el modulo auth. Se guarda el id, no la entidad (regla M1). */
    @Column(name = "usuario_id", nullable = false, updatable = false)
    private Long usuarioId;

    /** Colegio Medico del Peru. */
    @Column(nullable = false, length = 10, updatable = false)
    private String cmp;

    @Column(nullable = false, length = 100)
    private String nombres;

    @Column(name = "apellido_paterno", nullable = false, length = 60)
    private String apellidoPaterno;

    @Column(name = "apellido_materno", length = 60)
    private String apellidoMaterno;

    @Column(length = 20)
    private String telefono;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "especialidad_id", nullable = false)
    private Especialidad especialidad;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoMedico estado = EstadoMedico.ACTIVO;

    @OneToMany(mappedBy = "medico", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Horario> horarios = new ArrayList<>();

    protected Medico() {
        // Requerido por JPA.
    }

    protected Medico(Long usuarioId, String cmp, String nombres, String apellidoPaterno,
            String apellidoMaterno, Especialidad especialidad) {
        this.usuarioId = usuarioId;
        this.cmp = cmp;
        this.nombres = nombres;
        this.apellidoPaterno = apellidoPaterno;
        this.apellidoMaterno = apellidoMaterno;
        this.especialidad = especialidad;
        this.estado = EstadoMedico.ACTIVO;
    }

    // ---------- Polimorfismo ----------
    // Cada subclase responde distinto y nadie pregunta de que tipo es.

    /** GENERAL o ESPECIALISTA. Coincide con el valor del discriminador. */
    public abstract TipoMedico tipo();

    /**
     * Duracion sugerida de una consulta.
     *
     * Un especialista necesita mas tiempo por paciente que un medico general,
     * y de ahi sale la duracion por defecto del slot al crear su horario.
     */
    public abstract int duracionConsultaMinutos();

    /** Como se presenta el medico en la agenda y en los correos al paciente. */
    public abstract String descripcionProfesional();

    /**
     * Si puede atender una derivacion de otro medico.
     *
     * Solo el especialista recibe derivaciones; al general llega el paciente
     * por consulta directa.
     */
    public abstract boolean recibeDerivaciones();

    // ---------- Comportamiento comun ----------

    public void actualizarDatos(String nombres, String apellidoPaterno, String apellidoMaterno,
            String telefono) {
        this.nombres = nombres;
        this.apellidoPaterno = apellidoPaterno;
        this.apellidoMaterno = apellidoMaterno;
        this.telefono = telefono;
    }

    public void cambiarEspecialidad(Especialidad especialidad) {
        this.especialidad = especialidad;
    }

    public void cambiarEstado(EstadoMedico estado) {
        this.estado = estado;
    }

    public boolean admiteCitas() {
        return estado.admiteCitas();
    }

    public void agregarHorario(Horario horario) {
        horario.asignarMedico(this);
        horarios.add(horario);
    }

    public void quitarHorario(Horario horario) {
        horarios.remove(horario);
        horario.asignarMedico(null);
    }

    public void reemplazarHorarios(List<Horario> nuevos) {
        horarios.forEach(h -> h.asignarMedico(null));
        horarios.clear();
        nuevos.forEach(this::agregarHorario);
    }

    /** Apellidos y nombres, el orden de la agenda. */
    public String nombreCompleto() {
        return apellidoMaterno == null || apellidoMaterno.isBlank()
                ? "%s, %s".formatted(apellidoPaterno, nombres)
                : "%s %s, %s".formatted(apellidoPaterno, apellidoMaterno, nombres);
    }

    /** Como aparece en la cabecera de una receta: "Dr. Nombre Apellido - CMP 12345". */
    public String firmaProfesional() {
        return "%s %s %s - CMP %s".formatted(
                descripcionProfesional(), nombres, apellidoPaterno, cmp);
    }

    // ---------- Accesores ----------

    public Long getId() {
        return id;
    }

    public Long getUsuarioId() {
        return usuarioId;
    }

    public String getCmp() {
        return cmp;
    }

    public String getNombres() {
        return nombres;
    }

    public String getApellidoPaterno() {
        return apellidoPaterno;
    }

    public String getApellidoMaterno() {
        return apellidoMaterno;
    }

    public String getTelefono() {
        return telefono;
    }

    public Especialidad getEspecialidad() {
        return especialidad;
    }

    public EstadoMedico getEstado() {
        return estado;
    }

    public List<Horario> getHorarios() {
        return List.copyOf(horarios);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Medico otro)) {
            return false;
        }
        return id != null && id.equals(otro.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
