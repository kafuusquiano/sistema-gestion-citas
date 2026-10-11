package com.proyecto.gestioncitas.config.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import com.proyecto.gestioncitas.common.constants.KafkaTopics;

@Configuration
@Profile("dev")
public class KafkaTopicConfig {
    @Bean public NewTopic topicUsuarioRegistrado()      { return new NewTopic(KafkaTopics.USUARIO_REGISTRADO, 1, (short)1); }
    @Bean public NewTopic topicMedicoHorario()          { return new NewTopic(KafkaTopics.MEDICO_HORARIO_ACTUALIZADO, 1, (short)1); }
    @Bean public NewTopic topicCitaCreada()             { return new NewTopic(KafkaTopics.CITA_CREADA, 1, (short)1); }
    @Bean public NewTopic topicCitaCancelada()          { return new NewTopic(KafkaTopics.CITA_CANCELADA, 1, (short)1); }
    @Bean public NewTopic topicCitaReprogramada()       { return new NewTopic(KafkaTopics.CITA_REPROGRAMADA, 1, (short)1); }
    @Bean public NewTopic topicCitaCompletada()         { return new NewTopic(KafkaTopics.CITA_COMPLETADA, 1, (short)1); }
    @Bean public NewTopic topicLabResultado()           { return new NewTopic(KafkaTopics.LABORATORIO_RESULTADO, 1, (short)1); }
    @Bean public NewTopic topicLabCritico()             { return new NewTopic(KafkaTopics.LABORATORIO_CRITICO, 1, (short)1); }
}
