package com.proyecto.gestioncitas.common.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import com.proyecto.gestioncitas.common.constants.KafkaTopics;

@Slf4j
@Component
@Profile("dev")
@RequiredArgsConstructor
public class KafkaEventPublisher implements EventPublisher {
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Override
    public void publish(DomainEvent event) {
        String topic = KafkaTopics.fromTipo(event.getTipo());
        kafkaTemplate.send(topic, event.getEventId(), event);
        log.debug("Evento Kafka publicado: topic={}, id={}", topic, event.getEventId());
    }
}
