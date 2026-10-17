package com.proyecto.gestioncitas.notificacion.service.impl;

import com.proyecto.gestioncitas.notificacion.domain.enums.TipoNotificacion;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

/**
 * Convierte una plantilla de Thymeleaf y un conjunto de datos en el HTML del
 * correo.
 *
 * Las plantillas viven en resources/templates/email y se eligen por el tipo de
 * notificacion. Separar el texto del codigo permite corregir la redaccion de
 * un correo sin recompilar.
 *
 * Si una plantilla falta o falla, se devuelve un texto minimo en lugar de
 * propagar el error: es preferible que el paciente reciba un aviso escueto a
 * que no reciba ninguno.
 */
@Component
public class TemplateRenderer {

    private static final Logger log = LoggerFactory.getLogger(TemplateRenderer.class);

    private static final String CARPETA = "email/";

    private final TemplateEngine motor;

    public TemplateRenderer(TemplateEngine motor) {
        this.motor = motor;
    }

    public String renderizar(TipoNotificacion tipo, Map<String, Object> datos) {
        Context contexto = new Context(java.util.Locale.of("es", "PE"));
        contexto.setVariables(datos);

        try {
            return motor.process(CARPETA + tipo.getPlantilla(), contexto);
        } catch (RuntimeException e) {
            log.warn("No se pudo renderizar la plantilla {}: {}",
                    tipo.getPlantilla(), e.getMessage());
            return respaldo(tipo, datos);
        }
    }

    /** Texto minimo cuando la plantilla no esta disponible. */
    private String respaldo(TipoNotificacion tipo, Map<String, Object> datos) {
        String nombre = String.valueOf(datos.getOrDefault("nombre", "paciente"));
        return """
                <p>Estimado(a) %s:</p>
                <p>%s</p>
                <p>Centro de Salud Acapulco - DIRESA Callao</p>
                """.formatted(nombre, tipo.getAsunto());
    }
}
