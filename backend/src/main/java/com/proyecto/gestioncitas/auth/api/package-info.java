/**
 * Contrato publico del modulo auth.
 *
 * Spring Modulith expone por defecto solo el paquete raiz de cada modulo y
 * trata los subpaquetes como internos. Sin esta declaracion,
 * ApplicationModules.verify() rechazaria que paciente o medico importen
 * AuthFacade, que es justamente la via que la instruccion M1 manda usar.
 */
@org.springframework.modulith.NamedInterface("api")
package com.proyecto.gestioncitas.auth.api;
