package io.github.pansai.demo_20260107.dto;

import java.time.Instant;
import java.time.LocalDateTime;

public record ErrorResponse(
        String code,
        String message,
        String path,
        Instant timestamp
) {
}
