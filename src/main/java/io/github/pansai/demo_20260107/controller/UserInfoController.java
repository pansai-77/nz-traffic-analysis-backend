package io.github.pansai.demo_20260107.controller;

import io.github.pansai.demo_20260107.dto.UserInfoResponse;
import io.github.pansai.demo_20260107.entity.UserInfo;
import io.github.pansai.demo_20260107.service.UserInfoService;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/users")
public class UserInfoController {
    private final UserInfoService userInfoService;

    public UserInfoController(UserInfoService userInfoService){
        this.userInfoService = userInfoService;
    }

    @GetMapping("/getUserList")
    public List<UserInfoResponse> getUserList(){
        return userInfoService.getUserList()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @GetMapping("/getUserInfoByEmail")
    public UserInfoResponse getUserInfo(@RequestParam String email){
        UserInfo userByEmail = userInfoService.getUserInfo(email);
        if(userByEmail != null){
            return toResponse(userByEmail);
        } else {
            return null;
        }
    }

    @PostMapping("/createUser")
    public UserInfoResponse createUser(@RequestBody CreateUserRequest req){
        UserInfo newUser = new UserInfo();
        newUser.setUserName(req.userName());
        newUser.setBirthdate(req.birthdate());
        newUser.setSex(req.sex());
        newUser.setEmail(req.email());
        newUser.setStatus(1);

        UserInfo  saved = userInfoService.create(newUser);
        return toResponse(saved);
    }

    //DTO: UserInfoRequest
    public record CreateUserRequest(
            String userName,
            String email,
            Integer sex,
            LocalDate birthdate
    ) {}


    //
    private UserInfoResponse toResponse(UserInfo user){
        return new UserInfoResponse(
                user.getUserName(),
                user.getEmail(),
                user.getSex(),
                user.getBirthdate(),
                user.getStatus()
        );
    }
}
