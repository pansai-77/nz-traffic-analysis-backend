package io.github.pansai.traffic.dto.response;

public record LoginResponse(
        String token,
        String tokenType,
        Long userId,
        String userName,
        String userEmail
) {
}
