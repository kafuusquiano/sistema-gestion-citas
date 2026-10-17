package com.proyecto.gestioncitas.medico.api;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * Unica puerta de entrada al modulo medico (instruccion M1).
 *
 * La usa sobre todo cita: para calcular la disponibilidad necesita el horario
 * del medico en una fecha, pero no debe conocer HorarioRepository ni la
 * jerarquia de entidades.
 *
 * Los tipos que cruzan la frontera se declaran aqui como records anidados y
 * no exponen Horario ni Medico.
 */
public interface MedicoFacade {

    boolean existe(Long medicoId);

    /**
     * true si el medico existe y su estado admite citas nuevas.
     *
     * Un medico de LICENCIA sigue existiendo y conserva sus citas ya
     * agendadas, pero no acepta mas.
     */
    boolean admiteCitas(Long medicoId);

    /**
     * Franjas de atencion del medico en una fecha concreta.
     *
     * Ya tiene aplicadas las excepciones: si ese dia el medico esta de
     * vacaciones o es feriado, devuelve lista vacia. Cacheado 30 minutos e
     * invalidado por el evento medico.horario-actualizado (instruccion M4).
     */
    List<FranjaAtencion> obtenerFranjas(Long medicoId, LocalDate fecha);

    /**
     * @throws com.proyecto.gestioncitas.common.exception.ResourceNotFoundException
     *         si el medico no existe
     */
    ResumenMedico obtenerResumen(Long medicoId);

    /**
     * Registro de medico asociado a una cuenta.
     *
     * La necesita cita para /api/citas/agenda: el token trae el id del
     * usuario y hay que llegar al id del medico.
     *
     * @return id del medico, o null si esa cuenta no es de un medico
     */
    Long obtenerIdPorUsuario(Long usuarioId);

    /** Una franja de atencion con el tamano de turno que le corresponde. */
    record FranjaAtencion(
            LocalTime horaInicio,
            LocalTime horaFin,
            int duracionSlotMinutos) {
    }

    /** Lo que otro modulo puede ver de un medico. */
    record ResumenMedico(
            Long id,
            String cmp,
            String nombreCompleto,
            String especialidad,

            /** "Medico General" o "Especialista en Obstetricia". */
            String descripcionProfesional,

            /** Cabecera de receta, ya armada. */
            String firmaProfesional,

            /** Duracion que sugiere su tipo: 20 un general, 30 un especialista. */
            int duracionConsultaMinutos,

            boolean admiteCitas) {
    }
}
