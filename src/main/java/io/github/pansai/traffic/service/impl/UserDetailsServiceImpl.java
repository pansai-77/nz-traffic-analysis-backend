package io.github.pansai.traffic.service.impl;

import io.github.pansai.traffic.dao.UserInfoRepo;
import io.github.pansai.traffic.entity.UserInfoEntity;
import io.github.pansai.traffic.enums.UserStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service("userDetailsService")
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserInfoRepo userInfoRepo;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        //get user info
        UserInfoEntity userInfo = userInfoRepo.findByUserEmail(email);
        if (userInfo == null){
            throw new UsernameNotFoundException("Invalid! Please check your account");
        }

        //verify user status
        return User.withUsername(userInfo.getUserEmail())
                .password(userInfo.getUserPwdHash())
                .disabled(!UserStatus.ACTIVE.equals(userInfo.getUserStatus()))
                .authorities("ROLE_USER")
                .build();
    }
}
