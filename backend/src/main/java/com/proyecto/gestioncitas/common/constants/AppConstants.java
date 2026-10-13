package com.proyecto.gestioncitas.common.constants;

/**
 * Constantes transversales al sistema. No contiene logica.
 */
public final class AppConstants {

    private AppConstants() {
    }

    /** Zona horaria del Centro de Salud Acapulco, Callao. */
    public static final String ZONA_HORARIA = "America/Lima";

    /** Prefijo del header Authorization para los tokens JWT. */
    public static final String PREFIJO_BEARER = "Bearer ";

    /** Nombre del header que transporta el token. */
    public static final String HEADER_AUTORIZACION = "Authorization";

    /** Claim del token que guarda el rol del usuario. */
    public static final String CLAIM_ROL = "rol";

    /** Claim del token que distingue un refresh token de un access token. */
    public static final String CLAIM_TIPO = "tipo";

    public static final String TIPO_ACCESS = "access";
    public static final String TIPO_REFRESH = "refresh";

    // ---------- Nombres de cache ----------
    // El TTL de cada una se define en CacheConfig, no aqui.

    /** Slots libres de un medico en una fecha. Se invalida al mover una cita. */
    public static final String CACHE_SLOTS = "slotsDisponibles";

    /** Horario semanal de un medico. Se invalida al actualizar su horario. */
    public static final String CACHE_HORARIOS = "horariosMedico";

    /** Catalogo de especialidades, que cambia muy poco. */
    public static final String CACHE_ESPECIALIDADES = "especialidades";

    // ---------- Paginacion ----------

    public static final int PAGINA_DEFECTO = 0;
    public static final int TAMANO_PAGINA_DEFECTO = 20;
    public static final int TAMANO_PAGINA_MAXIMO = 100;

    // ---------- Reglas de negocio ----------

    /** Horas de anticipacion minima para que un paciente pueda cancelar. */
    public static final int HORAS_MINIMAS_CANCELACION = 24;

    /** Horas antes de la cita en que se envia el recordatorio. */
    public static final int HORAS_ANTES_RECORDATORIO = 24;

    /** Minutos de tolerancia tras la hora de la cita antes de marcar NO_ASISTIO. */
    public static final int MINUTOS_TOLERANCIA_INASISTENCIA = 30;
}
