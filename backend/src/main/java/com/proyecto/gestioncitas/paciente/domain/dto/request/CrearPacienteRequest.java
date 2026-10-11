package com.proyecto.gestioncitas.paciente.domain.dto.request;

import com.proyecto.gestioncitas.paciente.domain.enums.Sexo;
import com.proyecto.gestioncitas.paciente.domain.enums.TipoSangre;
import jakarta.validation.constraints.*;
import lombok.Data;
import java.time.LocalDate;

@Data
public class CrearPacienteRequest {
    @NotBlank(message = "El usuarioId es obligatorio")
    private String usuarioId;

    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    @NotBlank(message = "El apellido es obligatorio")
    private String apellido;

    @Email(message = "Email inválido")
    @NotBlank(message = "El email es obligatorio")
    private String email;

    @Pattern(regexp = "\\d{8}", message = "El DNI debe tener 8 dígitos")
    private String dni;

    private LocalDate fechaNacimiento;
    private Sexo sexo;

    @Pattern(regexp = "^[0-9+\\-\\s]{7,15}$", message = "Teléfono inválido")
    private String telefono;

    private String direccion;
    private TipoSangre tipoSangre;
    private String alergias;
}
