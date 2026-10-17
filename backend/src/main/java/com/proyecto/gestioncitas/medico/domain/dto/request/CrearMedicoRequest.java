package com.proyecto.gestioncitas.medico.domain.dto.request;

import com.proyecto.gestioncitas.common.util.DniValidator;
import com.proyecto.gestioncitas.medico.domain.enums.TipoMedico;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Cuerpo de POST /api/medicos. Solo ADMIN.
 *
 * Crea la cuenta de acceso y el medico en una sola operacion. La cuenta se
 * pide por AuthFacade, no construyendo un Usuario: eso seria importar una
 * entidad de otro modulo.
 *
 * El registro publico no sirve para medicos porque hay que verificar el CMP.
 */
public record CrearMedicoRequest(

        // ---------- Cuenta de acceso ----------

        @NotBlank(message = "El correo es obligatorio")
        @Email(message = "El correo no tiene un formato valido")
        @Size(max = 150)
        String email,

        @NotBlank(message = "El DNI es obligatorio")
        @Pattern(regexp = DniValidator.REGEX_DNI, message = "El DNI debe tener 8 digitos")
        String dni,

        @NotBlank(message = "La contrasena inicial es obligatoria")
        @Size(min = 8, max = 72, message = "La contrasena debe tener entre 8 y 72 caracteres")
        String password,

        // ---------- Datos profesionales ----------

        @NotNull(message = "El tipo de medico es obligatorio")
        TipoMedico tipo,

        @NotBlank(message = "El CMP es obligatorio")
        @Pattern(regexp = DniValidator.REGEX_CMP, message = "El CMP admite hasta 6 digitos")
        String cmp,

        @NotBlank(message = "Los nombres son obligatorios")
        @Size(max = 100)
        String nombres,

        @NotBlank(message = "El apellido paterno es obligatorio")
        @Size(max = 60)
        String apellidoPaterno,

        @Size(max = 60)
        String apellidoMaterno,

        @Size(max = 20)
        String telefono,

        @NotNull(message = "La especialidad es obligatoria")
        Long especialidadId,

        // ---------- Solo ESPECIALISTA ----------

        @Pattern(regexp = "\\d{1,6}|", message = "El RNE admite hasta 6 digitos")
        String rne,

        @Min(0) @Max(70)
        Short aniosExperiencia,

        // ---------- Solo GENERAL ----------

        @Size(max = 80)
        String areaRotacion) {

    /**
     * El RNE es obligatorio para un ESPECIALISTA y prohibido para un GENERAL.
     *
     * Se valida aqui y no solo en el constructor de la entidad para que el
     * error llegue como 422 con el campo marcado, en lugar de un 500. La misma
     * regla la refuerza ck_medicos_rne_por_tipo en la base de datos.
     */
    @AssertTrue(message = "Un ESPECIALISTA requiere RNE y un GENERAL no debe tenerlo")
    public boolean isRneCoherenteConElTipo() {
        if (tipo == null) {
            return true; // Ya lo reporta @NotNull sobre tipo.
        }
        boolean tieneRne = rne != null && !rne.isBlank();
        return tipo == TipoMedico.ESPECIALISTA ? tieneRne : !tieneRne;
    }
}
