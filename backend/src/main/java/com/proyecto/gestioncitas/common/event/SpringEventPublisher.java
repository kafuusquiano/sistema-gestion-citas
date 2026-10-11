package com.proyecto.gestioncitas.common.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Profile("prod")
@RequiredArgsConstructor
public class SpringEventPublisher implements EventPublisher {
    private final ApplicationEventPublisher publisher;

    @Override
    public void publish(DomainEvent event) {
        publisher.publishEvent(event);
        log.debug("Evento Spring publicado: tipo={}, id={}", event.getTipo(), event.getEventId());
    }
}
