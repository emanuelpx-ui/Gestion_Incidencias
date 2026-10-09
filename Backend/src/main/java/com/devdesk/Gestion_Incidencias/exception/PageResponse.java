package com.devdesk.Gestion_Incidencias.exception;

import org.springframework.data.domain.Page;
import java.util.List;
import java.util.function.Function;

public record PageResponse<T>(
        List<T> contenido,
        int pagina,
        int tamano,
        int totalPaginas,
        long totalItems,
        boolean esPrimera,
        boolean esUltima
) {
    public static <E, D> PageResponse<D> de(Page<E> page, Function<E, D> mapeador) {
        return new PageResponse<>(
                page.getContent().stream().map(mapeador).toList(),
                page.getNumber(),
                page.getSize(),
                page.getTotalPages(),
                page.getTotalElements(),
                page.isFirst(),
                page.isLast()
        );
    }

    public static <T> PageResponse<T> de(Page<T> page) {
        return de(page, Function.identity());
    }
}