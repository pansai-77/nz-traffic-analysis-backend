package io.github.pansai.traffic.service.impl;

import io.github.pansai.traffic.dao.UserActivationTokenRepo;
import io.github.pansai.traffic.dao.UserInfoRepo;
import io.github.pansai.traffic.dto.request.LoginRequest;
import io.github.pansai.traffic.dto.request.UserInfoRequest;
import io.github.pansai.traffic.dto.response.LoginResponse;
import io.github.pansai.traffic.entity.UserActivationToken;
import io.github.pansai.traffic.entity.UserInfoEntity;
import io.github.pansai.traffic.enums.ErrorCode;
import io.github.pansai.traffic.enums.UserStatus;
import io.github.pansai.traffic.handler.BusinessException;
import io.github.pansai.traffic.service.JwtAuthService;
import io.github.pansai.traffic.service.MailService;
import io.github.pansai.traffic.service.UserInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.List;

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

    @Autowired
    private JwtAuthService jwtAuthService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Value("${app.jwt.type}")
    private String jwtType;

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    /**
     * registerUser: verify input -> hash pwd -> save user -> generate activation token -> save activation token -> send email
     * @param userInfoRequest userinfo required: name, pwd, email; optional: birthdate
     */
    @Override
    @Transactional
    public void registerUser(UserInfoRequest userInfoRequest) {
        String email = userInfoRequest.userEmail().trim();

        if (userInfoRepo.existsByUserEmail(email)){
            throw new BusinessException(ErrorCode.USER_REGISTER_EMAIL_ALREADY_EXISTS);
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

        // generate activation token and expiresAt
        String activateToken = generateActivateToken();
        LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(30);

        // save activation information
        UserActivationToken userActivationToken = new UserActivationToken();
        userActivationToken.setUserId(newUser.getUserId());
        userActivationToken.setTokenInfo(activateToken);
        userActivationToken.setExpiresAt(expiresAt);
        userActivationTokenRepo.save(userActivationToken);

        //send activation email
        try{
            mailService.sendActivationMail(email, activateToken);
        }catch (Exception ex){
            ex.printStackTrace();
            throw ex;
        }
    }

    /**
     * activateUser: check token -> check token use status -> check expired -> update user status -> update activate token use time
     * @param token token info
     * @return success msg or fail msg
     */
    @Override
    @Transactional
    public void activateUser(String token) {
        UserActivationToken activationToken = userActivationTokenRepo.findByTokenInfo(token);
        // check whether exists
        if (activationToken == null){
            throw new BusinessException(ErrorCode.USER_ACTIVATION_TOKEN_NOT_EXISTS);
        }

        //check whether expired
        if(activationToken.getExpiresAt().isBefore(LocalDateTime.now())){
            throw new BusinessException(ErrorCode.USER_ACTIVATION_TOKEN_EXPIRED);
        }

        if (activationToken.getUsedAt() == null) {
            //update user status
            UserInfoEntity userInfo = userInfoRepo.findByUserId(activationToken.getUserId());
            userInfo.setUserStatus(UserStatus.ACTIVE);
            userInfoRepo.save(userInfo);

            //update token use time
            activationToken.setUsedAt(LocalDateTime.now());
            userActivationTokenRepo.save(activationToken);
        }
    }

    /**
     * resend activation email: find user -> get token -> verify use status and expire time -> generate new activation token -> send new activation email
     * @param email user email
     * @return success or fail msg
     */
    @Override
    public void resendActEmail(String email) {
        // find user
        UserInfoEntity user = userInfoRepo.findByUserEmail(email);
        if(user == null){
            throw new BusinessException(ErrorCode.USER_RESEND_USER_NOT_EXISTS);
        }
        if(user.getUserStatus() != UserStatus.PENDING){
            throw new BusinessException(ErrorCode.USER_RESEND_ALREADY_ACTIVATED);
        }

        // check whether the user already has a valid activation token
        UserActivationToken userActivationToken = userActivationTokenRepo.findValidTokenByUserId(user.getUserId());
        if(userActivationToken != null) {
            //Invalidate the previous token
            userActivationToken.setExpiresAt(LocalDateTime.now());
            userActivationTokenRepo.save(userActivationToken);
        }

        //generate new activation token
        String newActivateToken = generateActivateToken();
        LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(30);

        // save new activation information
        UserActivationToken newActivationToken = new UserActivationToken();
        newActivationToken.setUserId(user.getUserId());
        newActivationToken.setTokenInfo(newActivateToken);
        newActivationToken.setExpiresAt(expiresAt);
        userActivationTokenRepo.save(newActivationToken);

        //send new activation email
        try{
            mailService.sendActivationMail(email, newActivateToken);
        }catch (Exception ex) {
            ex.printStackTrace();
            throw ex;
        }
    }


    /**
     * loginUser: verify user( check exists - check status - check pwd ) -> generate login token -> return info
     * @param loginRequest user input email and pwd
     * @return loginResponse: loginToken, loginTokenType, userId, userName, userEmail
     */
    @Override
    public LoginResponse loginUserJwt(LoginRequest loginRequest) {
       String userEmail = loginRequest.userEmail();
       String userPwd = loginRequest.userPwd();

       // authenticationManager verify user login
       try {
           authenticationManager.authenticate( new UsernamePasswordAuthenticationToken(userEmail, userPwd));
       } catch (DisabledException ex) {
           throw new BusinessException(ErrorCode.USER_LOGIN_NOT_ACTIVATE);
       } catch (BadCredentialsException | UsernameNotFoundException ex) {
           throw new BusinessException(ErrorCode.USER_LOGIN_INFO_INVALID);
       }

        // get the user
        UserInfoEntity user = userInfoRepo.findByUserEmail(userEmail);
        if(user == null) {
            throw new BusinessException(ErrorCode.USER_LOGIN_INFO_INVALID);
        }

       // generate login token
       String loginToken = jwtAuthService.generateLoginToken(user.getUserId(), user.getUserEmail());

       //return info
       return new LoginResponse(
                loginToken,
                jwtType,
                user.getUserId(),
                user.getUserName(),
                user.getUserEmail()
        );
    }

    /**
     * private
     * generate activation token
     * @return activation token
     */
    private String generateActivateToken(){
        byte[] bytes = new byte[32];
        SECURE_RANDOM.nextBytes(bytes);
        StringBuilder stringBuilder = new StringBuilder(64);
        for (byte b : bytes){
            stringBuilder.append(String.format("%02x", b));
        }
        return stringBuilder.toString();
    }

}
