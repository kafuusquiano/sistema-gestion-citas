package com.proyecto.gestioncitas;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Punto de entrada del monolito modular.
 *
 * Los modulos de dominio (auth, paciente, medico, cita, historial, laboratorio,
 * notificacion) viven como subpaquetes y comparten este mismo contexto de Spring.
 * Se comunican solo por sus Facade publicas o por eventos de dominio.
 */
@SpringBootApplication
@EnableCaching
@EnableScheduling
@EnableAsync
public class GestionCitasApplication {

    public static void main(String[] args) {
        SpringApplication.run(GestionCitasApplication.class, args);
    }
}
