package com.proyecto.gestioncitas.paciente.domain.enums;

/**
 * Documento con el que se identifica el paciente en admision.
 *
 * El DNI cubre a la mayoria, pero el establecimiento atiende poblacion
 * migrante, de ahi el carne de extranjeria. El pasaporte es el caso residual.
 */
public enum TipoDocumento {

    DNI(8, 8),
    CARNE_EXTRANJERIA(9, 12),
    PASAPORTE(6, 12);

    private final int longitudMinima;
    private final int longitudMaxima;

    TipoDocumento(int longitudMinima, int longitudMaxima) {
        this.longitudMinima = longitudMinima;
        this.longitudMaxima = longitudMaxima;
    }

    public boolean longitudValida(String numero) {
        if (numero == null) {
            return false;
        }
        int n = numero.trim().length();
        return n >= longitudMinima && n <= longitudMaxima;
    }

    public int getLongitudMinima() {
        return longitudMinima;
    }

    public int getLongitudMaxima() {
        return longitudMaxima;
    }
}
