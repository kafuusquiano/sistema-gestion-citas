package com.proyecto.gestioncitas.common.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

/**
 * Implementacion unica de EventPublisher, sobre el bus de eventos interno de
 * Spring.
 *
 * El proyecto ya no usa Kafka, asi que no hay @Profile: esta implementacion
 * sirve igual en dev y en prod. Los consumidores son @EventListener; los que
 * ademas llevan @Async corren en el pool definido en AsyncConfig.
 */
@Component
public class SpringEventPublisher implements EventPublisher {

    private static final Logger log = LoggerFactory.getLogger(SpringEventPublisher.class);

    private final ApplicationEventPublisher publicador;

    public SpringEventPublisher(ApplicationEventPublisher publicador) {
        this.publicador = publicador;
    }

    @Override
    public void publicar(DomainEvent evento) {
        log.debug("Publicando evento {} con id {}", evento.tipo(), evento.eventId());
        publicador.publishEvent(evento);
    }
}
