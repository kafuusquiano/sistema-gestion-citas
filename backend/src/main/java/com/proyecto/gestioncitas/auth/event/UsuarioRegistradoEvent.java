package com.proyecto.gestioncitas.auth.event;

import com.proyecto.gestioncitas.auth.domain.enums.Rol;
import com.proyecto.gestioncitas.common.constants.EventosDominio;
import com.proyecto.gestioncitas.common.event.DomainEvent;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Se publico un usuario nuevo.
 *
 * Lo consumen dos modulos:
 *   - paciente, que crea la ficha clinica con los datos personales. Corre en
 *     la misma transaccion, asi que el registro es atomico: o quedan usuario
 *     y ficha, o no queda ninguno.
 *   - notificacion, que envia el correo de bienvenida. Va con @Async, porque
 *     nadie debe esperar al servidor SMTP para terminar de registrarse.
 *
 * El evento carga los datos personales a proposito. Es lo que permite que
 * auth no dependa de PacienteFacade, que seria una dependencia circular:
 * paciente ya necesita AuthFacade.
 */
public record UsuarioRegistradoEvent(
        UUID eventId,
        Instant ocurridoEn,
        Long usuarioId,
        String email,
        String dni,
        Rol rol,
        String nombres,
        String apellidoPaterno,
        String apellidoMaterno,
        LocalDate fechaNacimiento,
        String sexo,
        String telefono) implements DomainEvent {

    @Override
    public String tipo() {
        return EventosDominio.USUARIO_REGISTRADO;
    }

    public static UsuarioRegistradoEvent de(Long usuarioId, String email, String dni, Rol rol,
            String nombres, String apellidoPaterno, String apellidoMaterno,
            LocalDate fechaNacimiento, String sexo, String telefono) {
        return new UsuarioRegistradoEvent(
                UUID.randomUUID(), Instant.now(), usuarioId, email, dni, rol,
                nombres, apellidoPaterno, apellidoMaterno, fechaNacimiento, sexo, telefono);
    }

    /** Nombre completo para el saludo del correo de bienvenida. */
    public String nombreCompleto() {
        return apellidoMaterno == null || apellidoMaterno.isBlank()
                ? "%s %s".formatted(nombres, apellidoPaterno)
                : "%s %s %s".formatted(nombres, apellidoPaterno, apellidoMaterno);
    }
}
