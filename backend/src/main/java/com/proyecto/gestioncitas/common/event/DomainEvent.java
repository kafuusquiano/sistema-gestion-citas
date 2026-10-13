package com.proyecto.gestioncitas.common.event;

import java.time.Instant;
import java.util.UUID;

/**
 * Contrato de todo evento de dominio.
 *
 * Es el punto de abstraccion que permite que un modulo avise de algo sin
 * conocer a quien lo escucha. Los eventos concretos de cada modulo son records
 * que implementan esta interfaz, por ejemplo CitaCreadaEvent.
 *
 * El id y la fecha sirven para rastrear el evento en los logs y para
 * descartar duplicados si alguna vez se reprocesa.
 */
public interface DomainEvent {

    /** Identificador unico de esta ocurrencia concreta del evento. */
    UUID eventId();

    /** Nombre del evento, tomado de EventosDominio. Ejemplo: "cita.creada". */
    String tipo();

    /** Momento en que ocurrio el hecho, no en que se publico. */
    Instant ocurridoEn();
}
