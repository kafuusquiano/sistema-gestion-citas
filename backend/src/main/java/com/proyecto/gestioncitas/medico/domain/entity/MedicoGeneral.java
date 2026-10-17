package com.proyecto.gestioncitas.medico.domain.entity;

import com.proyecto.gestioncitas.medico.domain.enums.TipoMedico;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

/**
 * Medico general. Atiende la consulta ambulatoria del primer nivel.
 *
 * No tiene RNE: la restriccion ck_medicos_rne_por_tipo de V3__medico.sql lo
 * prohibe para este tipo, asi que esta subclase ni siquiera mapea esa columna.
 *
 * Lo propio de esta subclase es el area de rotacion, porque en un Centro de
 * Salud I-4 el medico general rota entre consultorios segun la demanda.
 */
@Entity
@DiscriminatorValue("GENERAL")
public class MedicoGeneral extends Medico {

    /** Consultorio o programa en el que rota: "Consulta externa", "CRED". */
    @Column(name = "area_rotacion", length = 80)
    private String areaRotacion;

    protected MedicoGeneral() {
        // Requerido por JPA.
    }

    public MedicoGeneral(Long usuarioId, String cmp, String nombres, String apellidoPaterno,
            String apellidoMaterno, Especialidad especialidad, String areaRotacion) {
        super(usuarioId, cmp, nombres, apellidoPaterno, apellidoMaterno, especialidad);
        this.areaRotacion = areaRotacion;
    }

    @Override
    public TipoMedico tipo() {
        return TipoMedico.GENERAL;
    }

    /**
     * 20 minutos, la duracion estandar de una consulta ambulatoria en el
     * primer nivel de atencion.
     */
    @Override
    public int duracionConsultaMinutos() {
        return 20;
    }

    @Override
    public String descripcionProfesional() {
        return "Medico General";
    }

    /** Al medico general llega el paciente por consulta directa, no derivado. */
    @Override
    public boolean recibeDerivaciones() {
        return false;
    }

    public void cambiarAreaRotacion(String areaRotacion) {
        this.areaRotacion = areaRotacion;
    }

    public String getAreaRotacion() {
        return areaRotacion;
    }
}
