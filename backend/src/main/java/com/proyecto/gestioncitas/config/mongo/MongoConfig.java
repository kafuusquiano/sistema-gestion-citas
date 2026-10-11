package com.proyecto.gestioncitas.config.mongo;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Configuration
@EnableMongoAuditing
@EnableMongoRepositories(basePackages = {
    "com.proyecto.gestioncitas.historial",
    "com.proyecto.gestioncitas.laboratorio"
})
public class MongoConfig {}
