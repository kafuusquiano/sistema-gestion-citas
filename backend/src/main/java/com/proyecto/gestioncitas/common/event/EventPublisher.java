package com.proyecto.gestioncitas.common.event;

public interface EventPublisher {
    void publish(DomainEvent event);
}
