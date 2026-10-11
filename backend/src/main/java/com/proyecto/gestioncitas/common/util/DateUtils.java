package com.proyecto.gestioncitas.common.util;

import com.proyecto.gestioncitas.common.constants.AppConstants;
import java.time.*;
import java.time.format.DateTimeFormatter;

public final class DateUtils {
    private static final ZoneId ZONA = ZoneId.of(AppConstants.ZONA_HORARIA);
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public static ZonedDateTime ahoraLima() { return ZonedDateTime.now(ZONA); }
    public static String formatear(Instant instant) {
        return instant == null ? "" : FMT.format(instant.atZone(ZONA));
    }
    private DateUtils() {}
}
