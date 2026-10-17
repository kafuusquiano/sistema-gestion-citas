/**
 * Codigo transversal del sistema: formato de respuesta, catalogo de errores,
 * contrato de eventos, auditoria y utilidades.
 *
 * Se declara como modulo ABIERTO porque no es un modulo de dominio sino un
 * nucleo compartido. Todos los demas dependen de el por diseno, y exigirle una
 * interfaz publica obligaria a declarar cada subpaquete uno por uno sin que
 * eso aportara aislamiento real: aqui no hay reglas de negocio que proteger.
 *
 * La direccion de la dependencia es siempre la misma, de los modulos hacia
 * common y nunca al reves, lo que mantiene el grafo sin ciclos.
 */
@org.springframework.modulith.ApplicationModule(
        type = org.springframework.modulith.ApplicationModule.Type.OPEN)
package com.proyecto.gestioncitas.common;
