/**
 * Eventos de dominio que publica medico.
 *
 * HorarioActualizadoEvent lo consume cita para invalidar su cache de slots.
 */
@org.springframework.modulith.NamedInterface("eventos")
package com.proyecto.gestioncitas.medico.event;
