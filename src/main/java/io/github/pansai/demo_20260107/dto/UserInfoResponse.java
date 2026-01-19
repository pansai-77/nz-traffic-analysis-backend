package io.github.pansai.demo_20260107.dto;

import java.time.LocalDate;

public record UserInfoResponse(
        String userName,
        String email,
        Integer sex,
        LocalDate birthdate,
        Integer status
) {
}
