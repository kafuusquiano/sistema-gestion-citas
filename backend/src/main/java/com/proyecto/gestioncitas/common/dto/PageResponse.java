package com.proyecto.gestioncitas.common.dto;

import java.util.List;
import org.springframework.data.domain.Page;

/**
 * Pagina serializable. Se usa en lugar de devolver un Page de Spring Data,
 * cuyo JSON no es estable entre versiones.
 */
public record PageResponse<T>(
        List<T> contenido,
        int pagina,
        int tamano,
        long totalElementos,
        int totalPaginas,
        boolean primera,
        boolean ultima) {

    public static <T> PageResponse<T> de(Page<T> page) {
        return new PageResponse<>(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isFirst(),
                page.isLast());
    }

    /**
     * Convierte una pagina de entidades en una pagina de DTOs sin perder los
     * metadatos de paginacion.
     */
    public static <E, D> PageResponse<D> de(Page<E> page, java.util.function.Function<E, D> mapeador) {
        return new PageResponse<>(
                page.getContent().stream().map(mapeador).toList(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isFirst(),
                page.isLast());
    }
}
