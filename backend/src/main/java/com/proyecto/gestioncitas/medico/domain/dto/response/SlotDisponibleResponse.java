package com.proyecto.gestioncitas.medico.domain.dto.response;

import lombok.Builder;
import lombok.Getter;
import java.time.LocalTime;

@Getter
@Builder
public class SlotDisponibleResponse {
    private final String medicoId;
    private final String fecha;
    private final LocalTime horaInicio;
    private final LocalTime horaFin;
    private final boolean disponible;
}
