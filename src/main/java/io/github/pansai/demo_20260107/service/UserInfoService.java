package io.github.pansai.demo_20260107.service;

import io.github.pansai.demo_20260107.entity.UserInfo;
import io.github.pansai.demo_20260107.repository.UserInfoRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class UserInfoService {

    private final UserInfoRepository repo;

    public UserInfoService(UserInfoRepository repo){
        this.repo = repo;
    }

    //获取所有用户
    public List<UserInfo> getUserList(){
        return repo.findAll();
    }

    //获取用户信息
    public UserInfo getUserInfo(String email){
        return repo.findByEmail(email);
    }

    //创建用户
    @Transactional
    public UserInfo create(UserInfo user){
        Set<Integer> valid_sex = Set.of(0, 1, 9);

        //check user exists
        if(user.getEmail() != null && repo.existsByEmail(user.getEmail())){
            throw new IllegalArgumentException("user already exists, please check your info");
        }

        //check user name
        if(user.getUserName() == null){
            throw new NullPointerException("user name cannot be null");
        }

        //check sex
        if(user.getSex() != null && ! valid_sex.contains(user.getSex())){
                throw new IllegalArgumentException(" Invalid sex fails");
        }

        //新增用户
        return repo.save(user);
    }
}
