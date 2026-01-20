package io.github.pansai.traffic.dto.request;

public record LoginRequest(
        String userEmail,
        String userPwd
) {
}
