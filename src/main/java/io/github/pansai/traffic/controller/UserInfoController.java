package io.github.pansai.traffic.controller;

import io.github.pansai.traffic.dto.request.LoginRequest;
import io.github.pansai.traffic.dto.request.UserInfoRequest;
import io.github.pansai.traffic.dto.response.LoginResponse;
import io.github.pansai.traffic.service.UserInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/userInfo")
public class UserInfoController {

    @Autowired
    private UserInfoService userInfoService;

    /**
     * register user
     * @param userInfoRequest required: name, pwd, email; optional: birthdate
     * @return register info: success msg or error msg
     */
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
