package io.github.pansai.traffic.controller;

import io.github.pansai.traffic.dto.request.UserInfoRequest;
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
     * @return
     */
    @PostMapping("/register")
    public String register(@RequestBody UserInfoRequest userInfoRequest){
        return userInfoService.registerUser(userInfoRequest);
    }

    @GetMapping("/activate")
    public String activate(@RequestParam String token){
        return userInfoService.activateUser(token);
    }

}
