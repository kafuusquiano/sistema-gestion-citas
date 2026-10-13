/**
 * Enums de auth compartidos con otros modulos.
 *
 * Rol aparece en los records publicos de AuthFacade y en UsuarioRegistradoEvent,
 * asi que quien use esa Facade o consuma ese evento necesita importarlo. Son
 * tipos de valor sin comportamiento ni dependencias, no entidades: exponerlos
 * no filtra nada del interior del modulo.
 *
 * La alternativa seria pasar el rol como texto, pero eso cambiaria un error de
 * compilacion por uno en tiempo de ejecucion.
 */
@org.springframework.modulith.NamedInterface("enums")
package com.proyecto.gestioncitas.auth.domain.enums;
