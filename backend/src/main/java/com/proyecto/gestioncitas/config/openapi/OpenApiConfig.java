package com.proyecto.gestioncitas.config.openapi;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Swagger UI con autenticacion Bearer (instruccion G8).
 *
 * El esquema de seguridad declarado aqui es lo que pone el boton Authorize en
 * /swagger-ui.html: sin el, cada endpoint protegido se prueba a mano pegando
 * el header, que es inviable para una API de este tamano.
 */
@Configuration
public class OpenApiConfig {

    private static final String ESQUEMA_BEARER = "bearerAuth";

    @Bean
    public OpenAPI openApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("API - Sistema de Gestion de Citas Medicas")
                        .description("""
                                Centro de Salud Acapulco, DIRESA Callao.
                                Monolito modular con Spring Boot. Roles: ADMIN, MEDICO y PACIENTE.

                                Para probar los endpoints protegidos: obtenga un token en
                                POST /api/auth/login y peguelo en el boton Authorize, sin el
                                prefijo 'Bearer'.
                                """)
                        .version("v1")
                        .contact(new Contact().name("Centro de Salud Acapulco"))
                        .license(new License().name("MIT")))
                .components(new Components()
                        .addSecuritySchemes(ESQUEMA_BEARER, new SecurityScheme()
                                .name(ESQUEMA_BEARER)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("Access token devuelto por /api/auth/login")))
                // Aplica el esquema a todos los endpoints; los publicos lo ignoran.
                .addSecurityItem(new SecurityRequirement().addList(ESQUEMA_BEARER));
    }
}
