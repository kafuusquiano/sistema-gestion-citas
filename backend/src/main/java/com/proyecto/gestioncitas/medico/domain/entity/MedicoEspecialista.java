package com.proyecto.gestioncitas.medico.domain.entity;

import com.proyecto.gestioncitas.medico.domain.enums.TipoMedico;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

/**
 * Medico especialista: obstetricia, odontologia y las demas especialidades
 * del alcance del proyecto.
 *
 * Lo que lo distingue es el RNE, el Registro Nacional de Especialista. La
 * restriccion ck_medicos_rne_por_tipo de V3__medico.sql lo exige para este
 * tipo y lo prohibe para el general, asi que la regla se cumple tambien a
 * nivel de base de datos y no solo en Java.
 */
@Entity
@DiscriminatorValue("ESPECIALISTA")
public class MedicoEspecialista extends Medico {

    /**
     * Registro Nacional de Especialista.
     *
     * La columna NO se declara nullable = false aunque el dato sea obligatorio
     * para esta subclase: con SINGLE_TABLE la comparten las filas de
     * MedicoGeneral, que la dejan nula. Quien garantiza la regla es el
     * constructor en Java y el CHECK ck_medicos_rne_por_tipo en la base.
     */
    @Column(length = 10)
    private String rne;

    @Column(name = "anios_experiencia")
    private Short aniosExperiencia;

    protected MedicoEspecialista() {
        // Requerido por JPA.
    }

    public MedicoEspecialista(Long usuarioId, String cmp, String nombres, String apellidoPaterno,
            String apellidoMaterno, Especialidad especialidad, String rne, Short aniosExperiencia) {
        super(usuarioId, cmp, nombres, apellidoPaterno, apellidoMaterno, especialidad);
        if (rne == null || rne.isBlank()) {
            throw new IllegalArgumentException("Un medico especialista requiere RNE");
        }
        this.rne = rne;
        this.aniosExperiencia = aniosExperiencia;
    }

    @Override
    public TipoMedico tipo() {
        return TipoMedico.ESPECIALISTA;
    }

    /**
     * 30 minutos: una consulta de especialidad toma mas que una general,
     * porque suele incluir revision de examenes previos.
     */
    @Override
    public int duracionConsultaMinutos() {
        return 30;
    }

    /** Incluye la especialidad, que es lo que el paciente necesita leer. */
    @Override
    public String descripcionProfesional() {
        return "Especialista en " + getEspecialidad().getNombre();
    }

    @Override
    public boolean recibeDerivaciones() {
        return true;
    }

    public void actualizarExperiencia(Short aniosExperiencia) {
        this.aniosExperiencia = aniosExperiencia;
    }

    public String getRne() {
        return rne;
    }

    public Short getAniosExperiencia() {
        return aniosExperiencia;
    }
}
