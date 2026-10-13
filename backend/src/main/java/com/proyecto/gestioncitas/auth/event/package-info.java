/**
 * Eventos de dominio que publica auth.
 *
 * Se expone porque los eventos son el otro canal legitimo de comunicacion
 * entre modulos, junto con la Facade (instruccion M1). Quien los consume
 * necesita importar la clase del evento.
 *
 * Lo que NO se expone es el resto del modulo: repositorios, entidades y DTOs
 * internos siguen siendo privados.
 */
@org.springframework.modulith.NamedInterface("eventos")
package com.proyecto.gestioncitas.auth.event;
