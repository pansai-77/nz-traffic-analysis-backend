package io.github.pansai.traffic.dto.request;

import java.time.Instant;
import java.time.LocalDate;

public record UserInfoRequest(
        String userName,
        String userPwd,
        String userEmail,
        LocalDate userBirthdate,
        Instant userStatus
) {}