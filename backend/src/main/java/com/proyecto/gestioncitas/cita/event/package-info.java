/**
 * Eventos de dominio que publica cita.
 *
 * Los consume notificacion para avisar al paciente de cada cambio que le
 * afecta: confirmacion, cancelacion, reprogramacion y atencion realizada.
 */
@org.springframework.modulith.NamedInterface("eventos")
package com.proyecto.gestioncitas.cita.event;
