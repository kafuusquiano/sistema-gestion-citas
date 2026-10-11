package com.proyecto.gestioncitas.paciente.domain.dto.response;

import com.proyecto.gestioncitas.paciente.domain.enums.Sexo;
import com.proyecto.gestioncitas.paciente.domain.enums.TipoSangre;
import lombok.Builder;
import lombok.Getter;
import java.time.LocalDate;

@Getter
@Builder
public class PacienteResponse {
    private final String id;
    private final String usuarioId;
    private final String nombre;
    private final String apellido;
    private final String email;
    private final String dni;
    private final LocalDate fechaNacimiento;
    private final Sexo sexo;
    private final String telefono;
    private final String direccion;
    private final TipoSangre tipoSangre;
    private final String alergias;
    private final boolean activo;
}
