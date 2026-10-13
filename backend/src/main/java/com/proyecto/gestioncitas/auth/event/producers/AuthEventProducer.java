package com.proyecto.gestioncitas.auth.event.producers;

import com.proyecto.gestioncitas.auth.domain.entity.Usuario;
import com.proyecto.gestioncitas.auth.event.UsuarioRegistradoEvent;
import com.proyecto.gestioncitas.common.event.EventPublisher;
import java.time.LocalDate;
import org.springframework.stereotype.Component;

/**
 * Publica los eventos del modulo auth.
 *
 * El servicio no llama a EventPublisher directamente: lo hace a traves de
 * este productor, para que todo lo que auth emite este en un solo archivo y
 * se vea de un vistazo.
 */
@Component
public class AuthEventProducer {

    private final EventPublisher publisher;

    public AuthEventProducer(EventPublisher publisher) {
        this.publisher = publisher;
    }

    public void usuarioRegistrado(Usuario usuario, String nombres, String apellidoPaterno,
            String apellidoMaterno, LocalDate fechaNacimiento, String sexo, String telefono) {

        publisher.publicar(UsuarioRegistradoEvent.de(
                usuario.getId(),
                usuario.getEmail(),
                usuario.getDni(),
                usuario.getRol(),
                nombres,
                apellidoPaterno,
                apellidoMaterno,
                fechaNacimiento,
                sexo,
                telefono));
    }
}
