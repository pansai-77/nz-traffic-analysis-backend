package io.github.pansai.traffic.service.impl;

import io.github.pansai.traffic.dao.UserActivationTokenRepo;
import io.github.pansai.traffic.dao.UserInfoRepo;
import io.github.pansai.traffic.dto.request.UserInfoRequest;
import io.github.pansai.traffic.entity.UserActivationToken;
import io.github.pansai.traffic.entity.UserInfoEntity;
import io.github.pansai.traffic.enums.UserStatus;
import io.github.pansai.traffic.service.MailService;
import io.github.pansai.traffic.service.UserInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Service("userInfoService")
public class UserInfoServiceImpl implements UserInfoService {

    @Autowired
    private UserInfoRepo userInfoRepo;

    @Autowired
    private UserActivationTokenRepo userActivationTokenRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private MailService mailService;

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();


    @Override
    @Transactional
    public String registerUser(UserInfoRequest userInfoRequest) {
        String email = userInfoRequest.userEmail().trim();

        if (userInfoRepo.existsByUserEmail(email)){
            throw new IllegalArgumentException("This email is already exists");
        }

        //define new user: hash pwd
        UserInfoEntity userInfo = new UserInfoEntity();
        userInfo.setUserName(userInfoRequest.userName());
        userInfo.setUserEmail(email);
        userInfo.setUserBirthdate(userInfoRequest.userBirthdate());
        userInfo.setUserPwdHash(passwordEncoder.encode(userInfoRequest.userPwd()));
        userInfo.setUserStatus(UserStatus.PENDING);

        //save new user
        UserInfoEntity newUser = userInfoRepo.save(userInfo);

        // generate token and expiresAt
        String token =generateToken();
        LocalDateTime expiresAt = LocalDateTime.now().plus(30, ChronoUnit.MINUTES);

        // save activation information
        UserActivationToken userActivationToken = new UserActivationToken();
        userActivationToken.setUserId(newUser.getUserId());
        userActivationToken.setTokenInfo(token);
        userActivationToken.setExpiresAt(expiresAt);
        userActivationTokenRepo.save(userActivationToken);

        //send activate email
        try{
            mailService.sendActivationMail(email, token);
        }catch (Exception ex){
            ex.printStackTrace();
            throw ex;
        }

        return "We have received your register and send a email to you. Please check email to activate. Thank you!";
    }

    /**
     * generate token
     * @return token
     */
    private String generateToken(){
        byte[] bytes = new byte[32];
        SECURE_RANDOM.nextBytes(bytes);
        StringBuilder stringBuilder = new StringBuilder(64);
        for (byte b : bytes){
            stringBuilder.append(String.format("%02x", b));
        }
        return stringBuilder.toString();
    }

    @Override
    @Transactional
    public String activateUser(String token) {
        UserActivationToken activationToken = userActivationTokenRepo.findByTokenInfo(token);
        // check whether exists
        if (activationToken == null){
            throw new IllegalArgumentException("Invalid token");
        }

        // check whether use
        if (activationToken.getUsedAt() != null) {
            throw new IllegalArgumentException("Token already used");
        }

        //check whether expired
        if(activationToken.getExpiresAt().isBefore(LocalDateTime.now())){
            throw new IllegalArgumentException("Token expired");
        }

        //update user status
        UserInfoEntity userInfo = userInfoRepo.findByUserId(activationToken.getUserId());
        userInfo.setUserStatus(UserStatus.ACTIVE);
        userInfoRepo.save(userInfo);

        //update token use time
        activationToken.setUsedAt(LocalDateTime.now());
        userActivationTokenRepo.save(activationToken);

        return "Your account have activated. You can login now! Enjoy!";
    }

}
