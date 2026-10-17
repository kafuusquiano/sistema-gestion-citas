package com.proyecto.gestioncitas.medico.api;

import com.proyecto.gestioncitas.medico.domain.entity.Horario;
import com.proyecto.gestioncitas.medico.domain.entity.Medico;
import com.proyecto.gestioncitas.medico.domain.enums.DiaSemana;
import com.proyecto.gestioncitas.medico.exception.MedicoNoEncontradoException;
import com.proyecto.gestioncitas.medico.repository.HorarioRepository;
import com.proyecto.gestioncitas.medico.repository.MedicoRepository;
import com.proyecto.gestioncitas.medico.service.HorarioService;
import java.time.LocalDate;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementacion de la fachada. Traduce las entidades a los records publicos,
 * de modo que Medico y Horario nunca salen del modulo.
 */
@Service
@Transactional(readOnly = true)
public class MedicoFacadeImpl implements MedicoFacade {

    private final MedicoRepository medicoRepository;
    private final HorarioRepository horarioRepository;
    private final HorarioService horarioService;

    public MedicoFacadeImpl(MedicoRepository medicoRepository,
            HorarioRepository horarioRepository,
            HorarioService horarioService) {
        this.medicoRepository = medicoRepository;
        this.horarioRepository = horarioRepository;
        this.horarioService = horarioService;
    }

    @Override
    public boolean existe(Long medicoId) {
        return medicoId != null && medicoRepository.existsById(medicoId);
    }

    @Override
    public boolean admiteCitas(Long medicoId) {
        if (medicoId == null) {
            return false;
        }
        return medicoRepository.findById(medicoId)
                .map(Medico::admiteCitas)
                .orElse(false);
    }

    @Override
    public List<FranjaAtencion> obtenerFranjas(Long medicoId, LocalDate fecha) {
        // Una excepcion que cubra la fecha deja el dia sin franjas, aunque la
        // plantilla semanal diga que atiende.
        if (horarioService.tieneExcepcion(medicoId, fecha)) {
            return List.of();
        }

        return horarioRepository
                .findByMedicoIdAndDiaSemanaAndActivoTrue(medicoId, DiaSemana.desde(fecha))
                .stream()
                .map(this::aFranja)
                .toList();
    }

    @Override
    public ResumenMedico obtenerResumen(Long medicoId) {
        Medico medico = medicoRepository.findById(medicoId)
                .orElseThrow(() -> new MedicoNoEncontradoException(medicoId));

        // Los tres ultimos campos salen de los metodos polimorficos: quien
        // consume la Facade recibe el dato correcto sin saber de que subclase
        // se trata.
        return new ResumenMedico(
                medico.getId(),
                medico.getCmp(),
                medico.nombreCompleto(),
                medico.getEspecialidad().getNombre(),
                medico.descripcionProfesional(),
                medico.firmaProfesional(),
                medico.duracionConsultaMinutos(),
                medico.admiteCitas());
    }

    @Override
    public Long obtenerIdPorUsuario(Long usuarioId) {
        if (usuarioId == null) {
            return null;
        }
        return medicoRepository.findByUsuarioId(usuarioId)
                .map(Medico::getId)
                .orElse(null);
    }

    private FranjaAtencion aFranja(Horario horario) {
        return new FranjaAtencion(
                horario.getHoraInicio(),
                horario.getHoraFin(),
                horario.getDuracionSlotMinutos());
    }
}
