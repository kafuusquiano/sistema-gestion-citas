package com.proyecto.gestioncitas.paciente.domain.dto.request;

import com.proyecto.gestioncitas.paciente.domain.enums.Seguro;
import com.proyecto.gestioncitas.paciente.domain.enums.Sexo;
import com.proyecto.gestioncitas.paciente.domain.enums.TipoDocumento;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.util.List;

/**
 * Cuerpo de POST /api/pacientes. Lo usa admision para registrar a quien llega
 * por ventanilla, con o sin cuenta en el sistema.
 *
 * No pide numero de historia: lo asigna el sistema con la secuencia de
 * V7__paciente_numero_historia.sql.
 */
public record CrearPacienteRequest(

        @NotNull(message = "El tipo de documento es obligatorio")
        TipoDocumento tipoDocumento,

        @NotBlank(message = "El numero de documento es obligatorio")
        @Pattern(regexp = "\\d{6,12}", message = "El numero de documento debe tener entre 6 y 12 digitos")
        String numeroDocumento,

        @NotBlank(message = "Los nombres son obligatorios")
        @Size(max = 100)
        String nombres,

        @NotBlank(message = "El apellido paterno es obligatorio")
        @Size(max = 60)
        String apellidoPaterno,

        @Size(max = 60)
        String apellidoMaterno,

        @NotNull(message = "La fecha de nacimiento es obligatoria")
        @Past(message = "La fecha de nacimiento debe ser anterior a hoy")
        LocalDate fechaNacimiento,

        @NotNull(message = "El sexo es obligatorio")
        Sexo sexo,

        /** Etiqueta del grupo sanguineo: "O+", "A-". Opcional en admision. */
        @Pattern(regexp = "O\\+|O-|A\\+|A-|B\\+|B-|AB\\+|AB-",
                message = "El grupo sanguineo debe ser uno de O+, O-, A+, A-, B+, B-, AB+ o AB-")
        String grupoSanguineo,

        @Size(max = 20)
        @Pattern(regexp = "\\d{6,20}|", message = "El telefono solo admite digitos")
        String telefono,

        @Email(message = "El correo no tiene un formato valido")
        @Size(max = 150)
        String email,

        @Size(max = 200)
        String direccion,

        @Size(max = 80)
        String distrito,

        Seguro seguro,

        @Valid
        List<ContactoEmergenciaRequest> contactos) {

    /** Contacto de emergencia enviado junto con la ficha. */
    public record ContactoEmergenciaRequest(

            @NotBlank(message = "El nombre del contacto es obligatorio")
            @Size(max = 150)
            String nombres,

            @NotBlank(message = "El parentesco es obligatorio")
            @Size(max = 40)
            String parentesco,

            @NotBlank(message = "El telefono del contacto es obligatorio")
            @Size(max = 20)
            String telefono,

            boolean esPrincipal) {
    }
}
