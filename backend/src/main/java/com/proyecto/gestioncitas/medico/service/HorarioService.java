package com.proyecto.gestioncitas.medico.service;

import com.proyecto.gestioncitas.medico.domain.dto.request.ExcepcionHorarioRequest;
import com.proyecto.gestioncitas.medico.domain.dto.request.HorarioRequest;
import com.proyecto.gestioncitas.medico.domain.dto.response.ExcepcionHorarioResponse;
import com.proyecto.gestioncitas.medico.domain.dto.response.HorarioResponse;
import java.time.LocalDate;
import java.util.List;

/**
 * Plantilla semanal de atencion y excepciones.
 */
public interface HorarioService {

    /**
     * Reemplaza la plantilla completa del medico.
     *
     * Es un reemplazo y no un alta parcial porque la pantalla del frontend
     * edita la semana entera: enviar solo los cambios obligaria a calcular
     * diferencias en el cliente y abriria la puerta a horarios cruzados.
     *
     * Publica medico.horario-actualizado para invalidar la cache de slots.
     */
    List<HorarioResponse> reemplazarHorarios(Long medicoId, List<HorarioRequest> horarios);

    /** Horario del medico cacheado 30 minutos (instruccion M4). */
    List<HorarioResponse> obtenerHorarios(Long medicoId);

    /** Franjas activas de un dia concreto. Lo usa el calculo de disponibilidad. */
    List<HorarioResponse> obtenerHorariosDeFecha(Long medicoId, LocalDate fecha);

    ExcepcionHorarioResponse registrarExcepcion(Long medicoId, ExcepcionHorarioRequest peticion);

    /** Excepcion que aplica a todo el establecimiento, como un feriado. */
    ExcepcionHorarioResponse registrarExcepcionGeneral(ExcepcionHorarioRequest peticion);

    List<ExcepcionHorarioResponse> listarExcepciones(Long medicoId);

    List<ExcepcionHorarioResponse> listarExcepcionesGenerales();

    void eliminarExcepcion(Long excepcionId);

    /** true si el medico no atiende esa fecha por una excepcion. */
    boolean tieneExcepcion(Long medicoId, LocalDate fecha);
}
