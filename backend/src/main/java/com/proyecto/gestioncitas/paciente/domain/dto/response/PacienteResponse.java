package com.proyecto.gestioncitas.paciente.domain.dto.response;

import com.proyecto.gestioncitas.paciente.domain.enums.EstadoPaciente;
import com.proyecto.gestioncitas.paciente.domain.enums.Seguro;
import com.proyecto.gestioncitas.paciente.domain.enums.Sexo;
import com.proyecto.gestioncitas.paciente.domain.enums.TipoDocumento;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

/**
 * Ficha completa del paciente, para la pantalla de detalle.
 */
public record PacienteResponse(
        Long id,
        Long usuarioId,
        String numeroHistoria,
        TipoDocumento tipoDocumento,
        String numeroDocumento,
        String nombres,
        String apellidoPaterno,
        String apellidoMaterno,
        String nombreCompleto,
        LocalDate fechaNacimiento,

        /** Calculada en la zona de Lima, no por el navegador. */
        int edad,

        Sexo sexo,

        /** Etiqueta "O+", no el nombre de la constante. */
        String grupoSanguineo,

        String telefono,
        String email,
        String direccion,
        String distrito,
        Seguro seguro,
        EstadoPaciente estado,
        boolean tieneCuenta,
        List<ContactoEmergenciaResponse> contactos,
        Instant createdAt) {

    public record ContactoEmergenciaResponse(
            Long id,
            String nombres,
            String parentesco,
            String telefono,
            boolean esPrincipal) {
    }
}
