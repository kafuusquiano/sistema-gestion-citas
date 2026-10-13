package com.proyecto.gestioncitas.config.web;

import com.proyecto.gestioncitas.common.constants.AppConstants;
import java.util.TimeZone;
import org.springframework.boot.jackson.autoconfigure.JsonMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.cfg.DateTimeFeature;

/**
 * Serializacion JSON de la API.
 *
 * Spring Boot 4 trae Jackson 3, que vive en el paquete tools.jackson. La
 * personalizacion ya no se hace con Jackson2ObjectMapperBuilder sino con
 * JsonMapperBuilderCustomizer, que recibe un JsonMapper.Builder.
 *
 * Jackson 3 ya incluye el soporte de java.time, asi que no hay que registrar
 * JavaTimeModule a mano como en Jackson 2.
 */
@Configuration
public class JacksonConfig {

    @Bean
    public JsonMapperBuilderCustomizer personalizarJson() {
        return builder -> builder
                // Fechas como texto ISO-8601, no como numero de milisegundos.
                // En Jackson 3 estas dos opciones salieron de SerializationFeature
                // y viven en DateTimeFeature, que es un DatatypeFeature.
                .disable(DateTimeFeature.WRITE_DATES_AS_TIMESTAMPS,
                        DateTimeFeature.WRITE_DURATIONS_AS_TIMESTAMPS)

                // Un campo desconocido en el cuerpo no debe tumbar la peticion:
                // permite que el frontend agregue campos sin romper el backend.
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)

                // Acepta un solo valor donde se espera un arreglo, util en los filtros.
                .enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY)

                // Las horas del establecimiento se muestran en la zona de Lima.
                .defaultTimeZone(TimeZone.getTimeZone(AppConstants.ZONA_HORARIA));
    }
}
