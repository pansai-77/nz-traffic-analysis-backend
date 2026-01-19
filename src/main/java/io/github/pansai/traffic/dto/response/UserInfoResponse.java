package io.github.pansai.traffic.dto.response;

import java.time.Instant;
import java.time.LocalDate;

public record UserInfoResponse(
        String userId,
        String userName,
        String userPwd,
        String userEmail,
        LocalDate userBirthdate,
        Instant userStatus,
        LocalDate createTime,
        LocalDate updateTime
) {
}
