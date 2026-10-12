package com.proyecto.gestioncitas.medico.domain.dto.response;

import com.proyecto.gestioncitas.medico.domain.enums.DiaSemana;
import lombok.Builder;
import lombok.Getter;
import java.time.LocalTime;

@Getter
@Builder
public class HorarioResponse {
    private final String id;
    private final String medicoId;
    private final DiaSemana diaSemana;
    private final LocalTime horaInicio;
    private final LocalTime horaFin;
    private final int duracionCitaMin;
    private final boolean activo;
}
