package com.proyecto.gestioncitas.common.constants;

/**
 * Nombres de los eventos de dominio.
 *
 * Reemplaza al antiguo KafkaTopics: el proyecto ya no usa Kafka, los eventos
 * viajan por ApplicationEventPublisher de Spring. Los nombres se conservan
 * porque identifican el evento en los logs y en el campo tipo de DomainEvent.
 */
public final class EventosDominio {

    private EventosDominio() {
    }

    public static final String USUARIO_REGISTRADO = "usuario.registrado";

    public static final String MEDICO_HORARIO_ACTUALIZADO = "medico.horario-actualizado";

    public static final String CITA_CREADA = "cita.creada";
    public static final String CITA_CANCELADA = "cita.cancelada";
    public static final String CITA_REPROGRAMADA = "cita.reprogramada";
    public static final String CITA_COMPLETADA = "cita.completada";

    public static final String LABORATORIO_RESULTADO_VALIDADO = "laboratorio.resultado-validado";
    public static final String LABORATORIO_VALOR_CRITICO = "laboratorio.valor-critico";
}
