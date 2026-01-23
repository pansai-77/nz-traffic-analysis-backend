package io.github.pansai.traffic.controller;

import io.github.pansai.traffic.dto.request.LoginRequest;
import io.github.pansai.traffic.dto.request.UserInfoRequest;
import io.github.pansai.traffic.dto.response.LoginResponse;
import io.github.pansai.traffic.enums.ErrorCode;
import io.github.pansai.traffic.handler.ApiResponse;
import io.github.pansai.traffic.service.UserInfoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/userInfo")
@Tag(name = "UserInfo", description = "User registration, activation and login")
public class UserInfoController {

    @Autowired
    private UserInfoService userInfoService;

    /**
     * register user
     * @param userInfoRequest required: name, pwd, email; optional: birthdate
     * @return register info: success msg or error msg
     */
    @Operation(summary = "User Register", description = "Create a new account")
    @PostMapping("/register")
    public ApiResponse<Void> register(@RequestBody UserInfoRequest userInfoRequest){
        userInfoService.registerUser(userInfoRequest);
        return ApiResponse.success(ErrorCode.SUCCESS);
    }

    /**
     * activate user
     * @param token token info
     * @return activate info: success msg or error msg
     */
    @Operation(summary = "User Activate", description = "Activate an account")
    @GetMapping("/activate")
    public ApiResponse<Void> activate(@RequestParam String token){
        userInfoService.activateUser(token);
        return ApiResponse.success(ErrorCode.SUCCESS);
    }


    /**
     * resend activation email
     * @param email user email
     * @return resend info: success msg or error msg
     */
    @GetMapping("/resendActEmail")
    public ApiResponse<Void> resendActEmail(@RequestParam String email){
        userInfoService.resendActEmail(email);
        return ApiResponse.success(ErrorCode.SUCCESS);
    }

    /**
     * login user
     * @param loginRequest user email and pwd
     * @return user info: user token, id, name and email
     */
    @PostMapping("/login")
    public ApiResponse<LoginResponse> login(@RequestBody LoginRequest loginRequest){
        LoginResponse loginResponse = userInfoService.loginUserJwt(loginRequest);
        return ApiResponse.success(loginResponse);
    }
}
