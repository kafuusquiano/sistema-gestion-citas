package com.proyecto.gestioncitas;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.security.autoconfigure.UserDetailsServiceAutoConfiguration;
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
/*
 * Se excluye UserDetailsServiceAutoConfiguration porque la autenticacion es
 * por JWT y no existe ningun UserDetailsService. Sin la exclusion, Spring Boot
 * crea un usuario en memoria con contrasena aleatoria y la imprime en el log
 * junto a un aviso de produccion. Ese usuario es inalcanzable (no hay formLogin
 * ni httpBasic), pero el mensaje confunde y la cuenta no deberia existir.
 */
@SpringBootApplication(exclude = UserDetailsServiceAutoConfiguration.class)
@EnableCaching
@EnableScheduling
@EnableAsync
public class GestionCitasApplication {

    public static void main(String[] args) {
        SpringApplication.run(GestionCitasApplication.class, args);
    }
}
