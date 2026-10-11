package com.proyecto.gestioncitas.common.util;

import com.proyecto.gestioncitas.common.constants.AppConstants;

public final class DniValidator {
    public static boolean esValido(String dni) {
        return dni != null && dni.matches("\\d{" + AppConstants.DNI_LONGITUD + "}");
    }
    private DniValidator() {}
}
