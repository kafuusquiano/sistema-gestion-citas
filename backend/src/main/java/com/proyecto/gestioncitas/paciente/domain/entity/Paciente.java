package com.proyecto.gestioncitas.paciente.domain.entity;

import com.proyecto.gestioncitas.common.audit.AuditableEntity;
import com.proyecto.gestioncitas.common.util.DateUtils;
import com.proyecto.gestioncitas.paciente.domain.enums.EstadoPaciente;
import com.proyecto.gestioncitas.paciente.domain.enums.GrupoSanguineo;
import com.proyecto.gestioncitas.paciente.domain.enums.Seguro;
import com.proyecto.gestioncitas.paciente.domain.enums.Sexo;
import com.proyecto.gestioncitas.paciente.domain.enums.TipoDocumento;
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
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Ficha del paciente. Mapea la tabla pacientes de V2__paciente.sql.
 *
 * usuarioId es opcional: admision registra pacientes de ventanilla que no
 * tienen cuenta en el sistema, y la cuenta se vincula despues si el paciente
 * quiere agendar en linea. Por eso el documento y los nombres viven tambien
 * aqui y no solo en la entidad Usuario del modulo auth.
 *
 * Se guarda el id del usuario y no una relacion @ManyToOne a Usuario: eso
 * seria importar una entidad de otro modulo y romper la regla M1. Para
 * consultar la cuenta se usa AuthFacade.
 */
@Entity
@Table(name = "pacientes")
public class Paciente extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Id de la cuenta en el modulo auth. Nulo si no tiene cuenta. */
    @Column(name = "usuario_id")
    private Long usuarioId;

    @Column(name = "numero_historia", nullable = false, length = 20, updatable = false)
    private String numeroHistoria;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_documento", nullable = false, length = 20)
    private TipoDocumento tipoDocumento = TipoDocumento.DNI;

    @Column(name = "numero_documento", nullable = false, length = 12)
    private String numeroDocumento;

    @Column(nullable = false, length = 100)
    private String nombres;

    @Column(name = "apellido_paterno", nullable = false, length = 60)
    private String apellidoPaterno;

    @Column(name = "apellido_materno", length = 60)
    private String apellidoMaterno;

    @Column(name = "fecha_nacimiento", nullable = false)
    private LocalDate fechaNacimiento;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private Sexo sexo;

    /** El converter anidado de GrupoSanguineo traduce a la etiqueta "O+". */
    @Column(name = "grupo_sanguineo", length = 5)
    private GrupoSanguineo grupoSanguineo;

    @Column(length = 20)
    private String telefono;

    @Column(length = 150)
    private String email;

    @Column(length = 200)
    private String direccion;

    @Column(length = 80)
    private String distrito;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Seguro seguro = Seguro.SIS;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoPaciente estado = EstadoPaciente.ACTIVO;

    /**
     * orphanRemoval borra el contacto cuando se quita de la lista: un
     * contacto de emergencia no existe sin su paciente.
     */
    @OneToMany(mappedBy = "paciente", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ContactoEmergencia> contactos = new ArrayList<>();

    protected Paciente() {
        // Requerido por JPA.
    }

    public Paciente(String numeroHistoria, TipoDocumento tipoDocumento, String numeroDocumento,
            String nombres, String apellidoPaterno, String apellidoMaterno,
            LocalDate fechaNacimiento, Sexo sexo) {
        this.numeroHistoria = numeroHistoria;
        this.tipoDocumento = tipoDocumento;
        this.numeroDocumento = numeroDocumento;
        this.nombres = nombres;
        this.apellidoPaterno = apellidoPaterno;
        this.apellidoMaterno = apellidoMaterno;
        this.fechaNacimiento = fechaNacimiento;
        this.sexo = sexo;
        this.seguro = Seguro.SIS;
        this.estado = EstadoPaciente.ACTIVO;
    }

    // ---------- Comportamiento ----------

    /** Vincula la ficha con una cuenta de acceso recien creada. */
    public void vincularUsuario(Long usuarioId) {
        this.usuarioId = usuarioId;
    }

    public void actualizarContacto(String telefono, String email, String direccion, String distrito) {
        this.telefono = telefono;
        this.email = email;
        this.direccion = direccion;
        this.distrito = distrito;
    }

    public void actualizarDatosClinicos(GrupoSanguineo grupoSanguineo, Seguro seguro) {
        this.grupoSanguineo = grupoSanguineo;
        if (seguro != null) {
            this.seguro = seguro;
        }
    }

    public void actualizarIdentidad(String nombres, String apellidoPaterno, String apellidoMaterno,
            LocalDate fechaNacimiento, Sexo sexo) {
        this.nombres = nombres;
        this.apellidoPaterno = apellidoPaterno;
        this.apellidoMaterno = apellidoMaterno;
        this.fechaNacimiento = fechaNacimiento;
        this.sexo = sexo;
    }

    public void dardeBaja() {
        this.estado = EstadoPaciente.INACTIVO;
    }

    public void reactivar() {
        this.estado = EstadoPaciente.ACTIVO;
    }

    public boolean puedeAgendar() {
        return estado.puedeAgendar();
    }

    public boolean tieneCuenta() {
        return usuarioId != null;
    }

    /**
     * Agrega un contacto y mantiene los dos lados de la relacion. Si llega
     * marcado como principal, desmarca el anterior: el indice parcial
     * uk_contactos_principal solo admite uno por paciente.
     */
    public void agregarContacto(ContactoEmergencia contacto) {
        if (contacto.isEsPrincipal()) {
            contactos.forEach(ContactoEmergencia::quitarPrincipal);
        }
        contacto.asignarPaciente(this);
        contactos.add(contacto);
    }

    public void quitarContacto(ContactoEmergencia contacto) {
        contactos.remove(contacto);
        contacto.asignarPaciente(null);
    }

    /** Apellidos y nombres, el orden en que lo escribe admision. */
    public String nombreCompleto() {
        return apellidoMaterno == null || apellidoMaterno.isBlank()
                ? "%s, %s".formatted(apellidoPaterno, nombres)
                : "%s %s, %s".formatted(apellidoPaterno, apellidoMaterno, nombres);
    }

    /** Edad en anos cumplidos, calculada en la zona de Lima. */
    public int edad() {
        return DateUtils.edad(fechaNacimiento);
    }

    // ---------- Accesores ----------

    public Long getId() {
        return id;
    }

    public Long getUsuarioId() {
        return usuarioId;
    }

    public String getNumeroHistoria() {
        return numeroHistoria;
    }

    public TipoDocumento getTipoDocumento() {
        return tipoDocumento;
    }

    public String getNumeroDocumento() {
        return numeroDocumento;
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

    public LocalDate getFechaNacimiento() {
        return fechaNacimiento;
    }

    public Sexo getSexo() {
        return sexo;
    }

    public GrupoSanguineo getGrupoSanguineo() {
        return grupoSanguineo;
    }

    public String getTelefono() {
        return telefono;
    }

    public String getEmail() {
        return email;
    }

    public String getDireccion() {
        return direccion;
    }

    public String getDistrito() {
        return distrito;
    }

    public Seguro getSeguro() {
        return seguro;
    }

    public EstadoPaciente getEstado() {
        return estado;
    }

    public List<ContactoEmergencia> getContactos() {
        return List.copyOf(contactos);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Paciente otro)) {
            return false;
        }
        return id != null && id.equals(otro.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
