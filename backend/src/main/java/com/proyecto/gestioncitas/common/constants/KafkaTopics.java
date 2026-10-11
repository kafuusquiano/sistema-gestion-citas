package com.proyecto.gestioncitas.common.constants;

import java.util.Map;

public final class KafkaTopics {
    public static final String USUARIO_REGISTRADO         = "usuario.registrado";
    public static final String MEDICO_HORARIO_ACTUALIZADO = "medico.horario-actualizado";
    public static final String CITA_CREADA                = "cita.creada";
    public static final String CITA_CANCELADA             = "cita.cancelada";
    public static final String CITA_REPROGRAMADA          = "cita.reprogramada";
    public static final String CITA_COMPLETADA            = "cita.completada";
    public static final String LABORATORIO_RESULTADO      = "laboratorio.resultado-validado";
    public static final String LABORATORIO_CRITICO        = "laboratorio.valor-critico";

    private static final Map<String, String> TIPO_TOPIC = Map.of(
        USUARIO_REGISTRADO,         USUARIO_REGISTRADO,
        MEDICO_HORARIO_ACTUALIZADO, MEDICO_HORARIO_ACTUALIZADO,
        CITA_CREADA,                CITA_CREADA,
        CITA_CANCELADA,             CITA_CANCELADA,
        CITA_REPROGRAMADA,          CITA_REPROGRAMADA,
        CITA_COMPLETADA,            CITA_COMPLETADA,
        LABORATORIO_RESULTADO,      LABORATORIO_RESULTADO,
        LABORATORIO_CRITICO,        LABORATORIO_CRITICO
    );

    public static String fromTipo(String tipo) {
        return TIPO_TOPIC.getOrDefault(tipo, tipo);
    }
    private KafkaTopics() {}
}
