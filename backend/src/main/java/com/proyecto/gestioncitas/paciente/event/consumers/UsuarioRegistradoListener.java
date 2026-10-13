package com.proyecto.gestioncitas.paciente.event.consumers;

import com.proyecto.gestioncitas.auth.domain.enums.Rol;
import com.proyecto.gestioncitas.auth.event.UsuarioRegistradoEvent;
import com.proyecto.gestioncitas.paciente.service.PacienteService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * Crea la ficha clinica cuando se registra un paciente.
 *
 * Es la segunda mitad del registro publico: auth crea la cuenta y publica el
 * evento, y este listener crea la ficha con los datos personales que el
 * evento transporta. Asi auth nunca llama a PacienteFacade, que seria una
 * dependencia circular.
 *
 * NO lleva @Async a proposito. Corre en el mismo hilo y en la misma
 * transaccion que creo la cuenta, de modo que el registro es atomico: si la
 * ficha falla, la cuenta tampoco queda. Con @Async quedarian cuentas sin
 * ficha, y ese usuario no podria agendar nada.
 *
 * El correo de bienvenida, en cambio, lo manda notificacion con @Async: ahi
 * si conviene, porque nadie debe esperar al servidor SMTP para registrarse.
 */
@Component
public class UsuarioRegistradoListener {

    private static final Logger log = LoggerFactory.getLogger(UsuarioRegistradoListener.class);

    private final PacienteService pacienteService;

    public UsuarioRegistradoListener(PacienteService pacienteService) {
        this.pacienteService = pacienteService;
    }

    @EventListener
    public void alRegistrarseUnUsuario(UsuarioRegistradoEvent evento) {
        // Un ADMIN o un MEDICO tambien generan este evento al crearse su
        // cuenta, y no les corresponde ficha de paciente.
        if (evento.rol() != Rol.PACIENTE) {
            log.debug("Evento {} ignorado: el rol {} no lleva ficha de paciente",
                    evento.eventId(), evento.rol());
            return;
        }

        log.debug("Creando ficha de paciente por el evento {}", evento.eventId());

        pacienteService.crearDesdeRegistro(
                evento.usuarioId(),
                evento.dni(),
                evento.nombres(),
                evento.apellidoPaterno(),
                evento.apellidoMaterno(),
                evento.fechaNacimiento(),
                evento.sexo(),
                evento.telefono(),
                evento.email());
    }
}
