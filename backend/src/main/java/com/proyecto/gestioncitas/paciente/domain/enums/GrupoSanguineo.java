package com.proyecto.gestioncitas.paciente.domain.enums;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * Grupo sanguineo y factor Rh.
 *
 * La etiqueta va en un campo y no en el nombre de la constante porque "O+" no
 * es un identificador valido en Java.
 *
 * Eso obliga al converter anidado: la restriccion CHECK de V2__paciente.sql
 * acepta 'O+', 'A-' y companía, asi que @Enumerated(STRING) escribiria
 * O_POSITIVO y la base rechazaria la fila. El converter traduce en ambos
 * sentidos y, al llevar autoApply, se aplica a todo campo de este tipo sin
 * anotar cada entidad.
 */
public enum GrupoSanguineo {

    O_POSITIVO("O+"),
    O_NEGATIVO("O-"),
    A_POSITIVO("A+"),
    A_NEGATIVO("A-"),
    B_POSITIVO("B+"),
    B_NEGATIVO("B-"),
    AB_POSITIVO("AB+"),
    AB_NEGATIVO("AB-");

    private final String etiqueta;

    GrupoSanguineo(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    public String getEtiqueta() {
        return etiqueta;
    }

    /**
     * Acepta la etiqueta ("O+") o el nombre de la constante ("O_POSITIVO"),
     * porque el frontend manda la primera y los seeds pueden usar cualquiera.
     *
     * @throws IllegalArgumentException si no corresponde a ningun grupo
     */
    public static GrupoSanguineo desdeEtiqueta(String valor) {
        if (valor == null || valor.isBlank()) {
            return null;
        }
        String limpio = valor.trim().toUpperCase();
        for (GrupoSanguineo g : values()) {
            if (g.etiqueta.equals(limpio) || g.name().equals(limpio)) {
                return g;
            }
        }
        throw new IllegalArgumentException("Grupo sanguineo no valido: " + valor);
    }

    /** Traduce entre la constante de Java y la etiqueta que guarda la columna. */
    @Converter(autoApply = true)
    public static class GrupoSanguineoConverter implements AttributeConverter<GrupoSanguineo, String> {

        @Override
        public String convertToDatabaseColumn(GrupoSanguineo grupo) {
            return grupo == null ? null : grupo.getEtiqueta();
        }

        @Override
        public GrupoSanguineo convertToEntityAttribute(String etiqueta) {
            return desdeEtiqueta(etiqueta);
        }
    }
}
