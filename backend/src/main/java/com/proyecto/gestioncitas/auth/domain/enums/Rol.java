package com.proyecto.gestioncitas.auth.domain.enums;

/**
 * Roles del sistema. La matriz de permisos esta en la seccion 2.4 del
 * documento del proyecto y se aplica con @PreAuthorize en cada controller.
 */
public enum Rol {

    /** Personal administrativo del establecimiento. Gestiona usuarios, medicos y horarios. */
    ADMIN,

    /** Profesional de la salud. Atiende sus citas y escribe en el historial clinico. */
    MEDICO,

    /** Paciente. Solo ve y gestiona lo suyo. */
    PACIENTE;

    /** Nombre de la authority que espera Spring Security. */
    public String comoAuthority() {
        return "ROLE_" + name();
    }
}
