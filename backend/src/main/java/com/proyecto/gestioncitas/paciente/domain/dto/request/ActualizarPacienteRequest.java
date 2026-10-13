package com.proyecto.gestioncitas.paciente.domain.dto.request;

import com.proyecto.gestioncitas.paciente.domain.enums.Seguro;
import com.proyecto.gestioncitas.paciente.domain.enums.Sexo;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

/**
 * Cuerpo de PUT /api/pacientes/{id} y de PUT /api/pacientes/me.
 *
 * No incluye el documento ni el numero de historia: cambiar el documento
 * seria otra persona, y el numero de historia no se reasigna nunca. Si hubo
 * un error de tipeo en el documento, lo corrige un ADMIN por otra via.
 */
public record ActualizarPacienteRequest(

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

        @Pattern(regexp = "O\\+|O-|A\\+|A-|B\\+|B-|AB\\+|AB-",
                message = "El grupo sanguineo debe ser uno de O+, O-, A+, A-, B+, B-, AB+ o AB-")
        String grupoSanguineo,

        @Size(max = 20)
        String telefono,

        @Email(message = "El correo no tiene un formato valido")
        @Size(max = 150)
        String email,

        @Size(max = 200)
        String direccion,

        @Size(max = 80)
        String distrito,

        Seguro seguro) {
}
