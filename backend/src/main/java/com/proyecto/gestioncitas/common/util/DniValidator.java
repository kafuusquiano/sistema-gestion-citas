package com.proyecto.gestioncitas.common.util;

import java.util.regex.Pattern;

/**
 * Validacion de documentos de identidad peruanos.
 *
 * El DNI del RENIEC son 8 digitos. No tiene digito verificador publico, asi
 * que la validacion se limita al formato. Se incluye el CMP porque todo medico
 * del establecimiento debe tener su numero de Colegio Medico del Peru.
 *
 * En los DTOs conviene usar @Pattern directamente; estos metodos son para las
 * validaciones de servicio y para los seeds.
 */
public final class DniValidator {

    private DniValidator() {
    }

    /** Expresion para usar en @Pattern(regexp = DniValidator.REGEX_DNI). */
    public static final String REGEX_DNI = "\\d{8}";

    /** Carne de extranjeria: entre 9 y 12 digitos. */
    public static final String REGEX_CARNE_EXTRANJERIA = "\\d{9,12}";

    /** CMP: hasta 6 digitos. */
    public static final String REGEX_CMP = "\\d{1,6}";

    private static final Pattern DNI = Pattern.compile(REGEX_DNI);
    private static final Pattern CARNE = Pattern.compile(REGEX_CARNE_EXTRANJERIA);
    private static final Pattern CMP = Pattern.compile(REGEX_CMP);

    public static boolean esDniValido(String dni) {
        return dni != null && DNI.matcher(dni).matches();
    }

    public static boolean esCarneExtranjeriaValido(String carne) {
        return carne != null && CARNE.matcher(carne).matches();
    }

    public static boolean esCmpValido(String cmp) {
        return cmp != null && CMP.matcher(cmp).matches();
    }

    /**
     * Normaliza lo que escribe el personal de admision: quita espacios, puntos
     * y guiones, y rellena con ceros a la izquierda hasta 8 digitos.
     *
     * Hace falta porque un DNI que empieza en cero se pierde al pasar por
     * Excel, y los seeds vienen de ahi.
     */
    public static String normalizarDni(String dni) {
        if (dni == null) {
            return null;
        }
        String limpio = dni.replaceAll("[\\s.\\-]", "");
        if (limpio.isEmpty() || !limpio.chars().allMatch(Character::isDigit)) {
            return limpio;
        }
        return limpio.length() < 8 ? "0".repeat(8 - limpio.length()) + limpio : limpio;
    }
}
