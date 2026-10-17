package com.proyecto.gestioncitas.notificacion.service.sender;

import com.proyecto.gestioncitas.notificacion.domain.entity.Notificacion;
import com.proyecto.gestioncitas.notificacion.domain.enums.CanalNotificacion;

/**
 * Entrega un aviso por un canal concreto.
 *
 * Es el punto de polimorfismo del modulo, y uno de los dos que sostienen el
 * pilar correspondiente de la seccion 1.3.1 del proyecto junto con la
 * jerarquia de Medico.
 *
 * El servicio que envia recorre las implementaciones disponibles, elige la que
 * declara el canal de la notificacion y llama a enviar(). En ningun punto
 * pregunta de que canal se trata ni usa un condicional sobre el enum: cada
 * implementacion responde lo suyo.
 *
 * La consecuencia practica se ve al agregar un canal nuevo, por ejemplo
 * mensajeria de texto: basta una clase mas que implemente esta interfaz y
 * declare su canal. Ni NotificacionServiceImpl ni los consumidores de eventos
 * cambian una linea.
 */
public interface NotificacionSender {

    /** Canal que atiende esta implementacion. */
    CanalNotificacion canal();

    /**
     * Entrega el aviso.
     *
     * @throws com.proyecto.gestioncitas.notificacion.exception.NotificacionException
     *         si la entrega falla; el servicio lo registra y reintenta despues
     */
    void enviar(Notificacion notificacion);

    /**
     * true si el canal puede operar ahora mismo.
     *
     * El correo depende de que haya un servidor configurado; el aviso dentro
     * de la aplicacion siempre puede entregarse. Comprobarlo antes evita
     * acumular intentos fallidos cuando se sabe de antemano que no hay forma
     * de entregar.
     */
    boolean disponible();
}
