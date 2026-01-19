package io.github.pansai.traffic.dto.response;

import java.time.Instant;

public record ErrorResponse(
        String code,
        String message,
        String path,
        Instant timestamp
) {
}
