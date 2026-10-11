package com.proyecto.gestioncitas.common.dto;

import lombok.Builder;
import lombok.Getter;
import java.time.Instant;
import java.util.List;

@Getter
@Builder
public class ErrorResponse {
    private final boolean success;
    private final String code;
    private final String message;
    private final List<String> errors;
    @Builder.Default
    private final Instant timestamp = Instant.now();
}
