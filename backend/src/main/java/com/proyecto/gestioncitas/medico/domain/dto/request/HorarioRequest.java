package com.proyecto.gestioncitas.medico.domain.dto.request;

import com.proyecto.gestioncitas.medico.domain.enums.DiaSemana;
import jakarta.validation.constraints.*;
import lombok.Data;
import java.time.LocalTime;

@Data
public class HorarioRequest {
    @NotNull(message = "El día es obligatorio")
    private DiaSemana diaSemana;

    @NotNull(message = "La hora de inicio es obligatoria")
    private LocalTime horaInicio;

    @NotNull(message = "La hora de fin es obligatoria")
    private LocalTime horaFin;

    @Min(value = 15, message = "La duración mínima es 15 minutos")
    @Max(value = 60, message = "La duración máxima es 60 minutos")
    private int duracionCitaMin = 30;
}
