package com.proyecto.gestioncitas.auth.domain.dto.request;

import com.proyecto.gestioncitas.common.util.DniValidator;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

/**
 * Cuerpo de POST /api/auth/register.
 *
 * El registro publico siempre crea un PACIENTE. Los medicos los da de alta un
 * ADMIN desde /api/medicos, porque hay que verificar su CMP.
 *
 * Trae los datos personales ademas de las credenciales: al registrarse se
 * publica usuario.registrado con esta informacion y el modulo paciente crea
 * la ficha al escucharlo. Asi auth nunca llama a PacienteFacade, que seria
 * una dependencia circular.
 */
public record RegisterRequest(

        @NotBlank(message = "El correo es obligatorio")
        @Email(message = "El correo no tiene un formato valido")
        @Size(max = 150, message = "El correo no puede exceder 150 caracteres")
        String email,

        @NotBlank(message = "El DNI es obligatorio")
        @Pattern(regexp = DniValidator.REGEX_DNI, message = "El DNI debe tener 8 digitos")
        String dni,

        @NotBlank(message = "La contrasena es obligatoria")
        @Size(min = 8, max = 72, message = "La contrasena debe tener entre 8 y 72 caracteres")
        String password,

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

        @NotBlank(message = "El sexo es obligatorio")
        @Pattern(regexp = "MASCULINO|FEMENINO", message = "El sexo debe ser MASCULINO o FEMENINO")
        String sexo,

        @Size(max = 20)
        String telefono) {

    // El limite de 72 caracteres de la contrasena no es arbitrario: BCrypt
    // ignora en silencio todo lo que pase de 72 bytes, asi que aceptar mas
    // daria una falsa sensacion de seguridad.
}
