package com.proyecto.gestioncitas.auth.event;

import com.proyecto.gestioncitas.common.event.DomainEvent;
import lombok.Getter;

@Getter
public class UsuarioRegistradoEvent extends DomainEvent {
    private final String usuarioId;
    private final String email;
    private final String nombre;
    private final String rol;

    public UsuarioRegistradoEvent(String usuarioId, String email, String nombre, String rol) {
        super("usuario.registrado");
        this.usuarioId = usuarioId;
        this.email     = email;
        this.nombre    = nombre;
        this.rol       = rol;
    }
}
