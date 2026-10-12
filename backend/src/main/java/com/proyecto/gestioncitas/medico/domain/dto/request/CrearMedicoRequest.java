package com.proyecto.gestioncitas.medico.domain.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class CrearMedicoRequest {
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

    private String codigoCmp;
    private String telefono;

    @NotBlank(message = "La especialidad es obligatoria")
    private String especialidadId;
}
