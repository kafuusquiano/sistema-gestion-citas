package com.proyecto.gestioncitas.paciente.api;

/**
 * Unica puerta de entrada al modulo paciente (instruccion M1).
 *
 * La usa sobre todo el modulo cita, que al agendar necesita comprobar que el
 * paciente existe y esta activo, pero no debe conocer PacienteRepository ni
 * la entidad Paciente. Tambien la usan historial y laboratorio para mostrar
 * de quien es una atencion o un resultado.
 */
public interface PacienteFacade {

    /** true si la ficha existe, sin importar su estado. */
    boolean existe(Long pacienteId);

    /**
     * true si la ficha existe y esta activa.
     *
     * Es la comprobacion que hace falta antes de agendar: un paciente dado de
     * baja sigue existiendo, porque su historial clinico no se borra, pero no
     * puede tomar citas nuevas.
     */
    boolean puedeAgendar(Long pacienteId);

    /**
     * Datos minimos para mostrar de quien es una cita o un resultado.
     *
     * @throws com.proyecto.gestioncitas.common.exception.ResourceNotFoundException
     *         si la ficha no existe
     */
    ResumenPaciente obtenerResumen(Long pacienteId);

    /**
     * Ficha asociada a una cuenta de acceso.
     *
     * La necesita el modulo cita para /api/citas/mis-citas: el token trae el
     * id del usuario, y hay que llegar al id del paciente.
     *
     * @return id del paciente, o null si esa cuenta no tiene ficha
     */
    Long obtenerIdPorUsuario(Long usuarioId);

    /**
     * Cuenta de acceso asociada a una ficha.
     *
     * Es el camino inverso del anterior y lo necesita notificacion: los
     * eventos de cita traen el id del paciente, pero los avisos se dirigen al
     * usuario. Devuelve null cuando la ficha no tiene cuenta, que es el caso
     * de quien se registro por ventanilla: a esa persona no hay a donde
     * escribirle.
     *
     * @return id del usuario, o null si la ficha no tiene cuenta
     */
    Long obtenerUsuarioIdPorPaciente(Long pacienteId);

    /** Lo que otro modulo puede ver de un paciente. */
    record ResumenPaciente(
            Long id,
            String numeroHistoria,
            String numeroDocumento,
            String nombreCompleto,
            int edad,
            String sexo,
            String email,
            boolean activo) {
    }
}
