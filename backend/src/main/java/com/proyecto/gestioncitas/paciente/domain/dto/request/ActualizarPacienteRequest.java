package com.proyecto.gestioncitas.paciente.domain.dto.request;

import com.proyecto.gestioncitas.paciente.domain.enums.Sexo;
import com.proyecto.gestioncitas.paciente.domain.enums.TipoSangre;
import lombok.Data;
import java.time.LocalDate;

@Data
public class ActualizarPacienteRequest {
    private String telefono;
    private String direccion;
    private LocalDate fechaNacimiento;
    private Sexo sexo;
    private TipoSangre tipoSangre;
    private String alergias;
}
