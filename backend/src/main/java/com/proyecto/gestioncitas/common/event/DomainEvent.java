package com.proyecto.gestioncitas.common.event;

import lombok.Getter;
import java.time.Instant;
import java.util.UUID;

@Getter
public abstract class DomainEvent {
    private final String eventId = UUID.randomUUID().toString();
    private final String tipo;
    private final Instant fecha = Instant.now();

    protected DomainEvent(String tipo) {
        this.tipo = tipo;
    }
}
