package com.proyecto.gestioncitas.config.mongo;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.EnableMongoAuditing;

/**
 * MongoDB, que solo almacena el historial clinico y los resultados de
 * laboratorio (instruccion M3).
 *
 * @EnableMongoAuditing hace que @CreatedDate y @LastModifiedDate funcionen en
 * los documentos. Es el equivalente de lo que AuditableEntity hace a mano en
 * las entidades JPA: aqui si se usa la infraestructura de Spring Data, porque
 * los documentos no comparten una superclase mapeada.
 *
 * El resto (cliente, plantilla y conversores de java.time) lo configura la
 * autoconfiguracion a partir de MONGO_URI. Los indices se crean con el script
 * infra/mongo/init/01-indices.js y no con @Indexed, para que esten explicitos
 * y versionados.
 */
@Configuration
@EnableMongoAuditing
public class MongoConfig {
}
