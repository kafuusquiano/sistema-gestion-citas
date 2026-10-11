package com.proyecto.gestioncitas.common.exception;

import lombok.Getter;

@Getter
public class ResourceNotFoundException extends RuntimeException {
    private final String code;
    public ResourceNotFoundException(String recurso, String campo, Object valor) {
        super(String.format("%s no encontrado con %s: %s", recurso, campo, valor));
        this.code = recurso.toUpperCase().replace(" ", "_") + "_NO_ENCONTRADO";
    }
}
