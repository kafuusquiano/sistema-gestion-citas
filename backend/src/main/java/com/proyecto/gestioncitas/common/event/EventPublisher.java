package com.proyecto.gestioncitas.common.event;

/**
 * Unica via por la que un modulo publica un evento de dominio.
 *
 * Los servicios dependen de esta interfaz y nunca del mecanismo concreto.
 * Gracias a eso, el dia que el transporte cambie (por ejemplo a una cola
 * externa) no se toca una sola linea de logica de negocio: basta otra
 * implementacion de esta interfaz.
 */
public interface EventPublisher {

    /**
     * Publica el evento. La entrega es asincrona respecto del consumidor, pero
     * la llamada no bloquea al publicador.
     */
    void publicar(DomainEvent evento);
}
