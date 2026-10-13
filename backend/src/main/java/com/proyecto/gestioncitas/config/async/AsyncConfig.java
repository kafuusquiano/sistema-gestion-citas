package com.proyecto.gestioncitas.config.async;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * Pool para el trabajo que no debe hacer esperar al usuario, sobre todo el
 * envio de correos del modulo notificacion.
 *
 * Importa porque los eventos de dominio ahora se entregan con @EventListener
 * de Spring: sin @Async, el consumidor correria en el mismo hilo que publico
 * el evento, y agendar una cita tardaria lo que tarde el SMTP en responder.
 *
 * El pool es chico a proposito: Render free da 512 MB y 0.1 CPU, asi que mas
 * hilos solo agregarian cambios de contexto.
 */
@Configuration
public class AsyncConfig implements AsyncConfigurer {

    private static final Logger log = LoggerFactory.getLogger(AsyncConfig.class);

    @Override
    @Bean(name = "taskExecutor")
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(4);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("async-notif-");

        // Si la cola se llena, lo ejecuta el hilo que lo encolo. Es preferible
        // a descartar una notificacion de valor critico de laboratorio.
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());

        // Al apagar, espera a terminar lo encolado: evita perder correos
        // cuando Render suspende el servicio por inactividad.
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(20);

        executor.initialize();
        return executor;
    }

    /**
     * Un metodo @Async que devuelve void se traga la excepcion sin este
     * handler: el fallo de un correo quedaria invisible en los logs.
     */
    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return (ex, metodo, parametros) ->
                log.error("Error no capturado en el metodo asincrono {}", metodo.getName(), ex);
    }
}
