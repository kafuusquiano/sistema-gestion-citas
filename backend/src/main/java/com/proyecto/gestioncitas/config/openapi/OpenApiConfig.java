package com.proyecto.gestioncitas.config.openapi;

import io.swagger.v3.oas.models.*;
import io.swagger.v3.oas.models.info.*;
import io.swagger.v3.oas.models.security.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("Sistema de Gestión de Citas Médicas")
                .description("API REST — Arquitectura Monolítica Modular")
                .version("1.0.0")
                .contact(new Contact().name("Equipo").email("dev@proyecto.com")))
            .addSecurityItem(new SecurityRequirement().addList("BearerAuth"))
            .components(new Components().addSecuritySchemes("BearerAuth",
                new SecurityScheme()
                    .type(SecurityScheme.Type.HTTP)
                    .scheme("bearer")
                    .bearerFormat("JWT")));
    }
}
