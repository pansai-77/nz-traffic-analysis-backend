package io.github.pansai.traffic.service;

import io.github.pansai.traffic.dto.request.UserInfoRequest;

public interface UserInfoService {

    /**
     * user register
     * @param request userinfo
     */
    String registerUser(UserInfoRequest request);

    /**
     * user activate
     * @param token token info
     */
    String activateUser(String token);
}
