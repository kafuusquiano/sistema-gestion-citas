package com.proyecto.gestioncitas.medico.event;

import com.proyecto.gestioncitas.common.event.DomainEvent;
import lombok.Getter;

@Getter
public class MedicoHorarioActualizadoEvent extends DomainEvent {
    private final String medicoId;

    public MedicoHorarioActualizadoEvent(String medicoId) {
        super("medico.horario-actualizado");
        this.medicoId = medicoId;
    }
}
