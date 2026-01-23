package io.github.pansai.traffic.service;

import io.github.pansai.traffic.dto.request.LoginRequest;
import io.github.pansai.traffic.dto.request.UserInfoRequest;
import io.github.pansai.traffic.dto.response.LoginResponse;

public interface UserInfoService {

    /**
     * user register
     * @param request userinfo
     * @return success or fail msg
     */
    void registerUser(UserInfoRequest request);


    /**
     *  user activate
     * @param token token info
     * @return success or fail msg
     */
    void activateUser(String token);

    /**
     * resend activation email
     * @param email user email
     * @return success or fail msg
     */
    void resendActEmail(String email);

    /**
     * user login (json web token) based on token
     * @param loginRequest user input email and pwd
     * @return token, token type, user id, user email, user name
     */
    LoginResponse loginUserJwt(LoginRequest loginRequest);
}
