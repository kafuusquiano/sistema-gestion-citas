package com.proyecto.gestioncitas.common.util;

import com.proyecto.gestioncitas.common.constants.AppConstants;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Fechas y horas en la zona del establecimiento, America/Lima.
 *
 * La base de datos guarda instantes en UTC. Todo lo que se muestra al usuario
 * o se compara contra un horario de atencion pasa por aqui, para que un
 * servidor en otra zona (Render) no corra las citas de hora.
 */
public final class DateUtils {

    private DateUtils() {
    }

    public static final ZoneId ZONA = ZoneId.of(AppConstants.ZONA_HORARIA);

    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter FORMATO_FECHA_HORA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final DateTimeFormatter FORMATO_HORA = DateTimeFormatter.ofPattern("HH:mm");
    private static final DateTimeFormatter FORMATO_FECHA_LARGA =
            DateTimeFormatter.ofPattern("EEEE d 'de' MMMM 'de' yyyy", Locale.of("es", "PE"));

    /** Fecha de hoy en Lima, no en la zona del servidor. */
    public static LocalDate hoy() {
        return LocalDate.now(ZONA);
    }

    public static LocalDateTime ahora() {
        return LocalDateTime.now(ZONA);
    }

    public static Instant aInstante(LocalDateTime fechaHora) {
        return fechaHora.atZone(ZONA).toInstant();
    }

    public static LocalDateTime aLocal(Instant instante) {
        return LocalDateTime.ofInstant(instante, ZONA);
    }

    public static String formatearFecha(LocalDate fecha) {
        return fecha == null ? null : fecha.format(FORMATO_FECHA);
    }

    public static String formatearFechaHora(LocalDateTime fechaHora) {
        return fechaHora == null ? null : fechaHora.format(FORMATO_FECHA_HORA);
    }

    public static String formatearHora(LocalTime hora) {
        return hora == null ? null : hora.format(FORMATO_HORA);
    }

    /** Para los correos: "lunes 13 de octubre de 2026". */
    public static String formatearFechaLarga(LocalDate fecha) {
        return fecha == null ? null : fecha.format(FORMATO_FECHA_LARGA);
    }

    public static boolean esPasado(LocalDateTime fechaHora) {
        return fechaHora.isBefore(ahora());
    }

    /** Horas que faltan para la fecha dada. Negativo si ya paso. */
    public static long horasHasta(LocalDateTime fechaHora) {
        return Duration.between(ahora(), fechaHora).toHours();
    }

    /** Edad en anos cumplidos, para la ficha del paciente. */
    public static int edad(LocalDate fechaNacimiento) {
        if (fechaNacimiento == null) {
            return 0;
        }
        return java.time.Period.between(fechaNacimiento, hoy()).getYears();
    }
}
