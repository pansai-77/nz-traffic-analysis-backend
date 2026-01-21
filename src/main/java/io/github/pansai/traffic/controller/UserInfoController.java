package io.github.pansai.traffic.controller;

import io.github.pansai.traffic.dto.request.LoginRequest;
import io.github.pansai.traffic.dto.request.UserInfoRequest;
import io.github.pansai.traffic.dto.response.LoginResponse;
import io.github.pansai.traffic.service.UserInfoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
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
    @Operation(
        summary = "User Register",
        description = "Create a new account",
        responses = {
            @ApiResponse(responseCode = "200", description = "We have received your register and sent an activation email to you. Please check your email. Thank you!"),
            @ApiResponse(responseCode = "400", description = "This email is already exists"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
        }
    )
    @PostMapping("/register")
    public String register(@RequestBody UserInfoRequest userInfoRequest){
        return userInfoService.registerUser(userInfoRequest);
    }

    /**
     * activate user
     * @param token token info
     * @return activate info: success msg or error msg
     */
    @GetMapping("/activate")
    public String activate(@RequestParam String token){
        return userInfoService.activateUser(token);
    }


    /**
     * resend activation email
     * @param email user email
     * @return resend info: success msg or error msg
     */
    @GetMapping("/resendActEmail")
    public String resendActEmail(@RequestParam String email){
        return userInfoService.resendActEmail(email);
    }

    /**
     * login user
     * @param loginRequest user email and pwd
     * @return user info: user token, id, name and email
     */
    @PostMapping("/login")
    public LoginResponse activate(@RequestBody LoginRequest loginRequest){
        return userInfoService.loginUserJwt(loginRequest);
    }

    @GetMapping("/ping")
    public String ping() {
        return "ok";
    }
}
