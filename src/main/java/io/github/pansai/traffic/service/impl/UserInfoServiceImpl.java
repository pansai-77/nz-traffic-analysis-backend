package io.github.pansai.traffic.service.impl;

import io.github.pansai.traffic.dao.UserActivationTokenRepo;
import io.github.pansai.traffic.dao.UserInfoRepo;
import io.github.pansai.traffic.dto.request.LoginRequest;
import io.github.pansai.traffic.dto.request.UserInfoRequest;
import io.github.pansai.traffic.dto.response.LoginResponse;
import io.github.pansai.traffic.entity.UserActivationToken;
import io.github.pansai.traffic.entity.UserInfoEntity;
import io.github.pansai.traffic.enums.UserStatus;
import io.github.pansai.traffic.service.MailService;
import io.github.pansai.traffic.service.UserInfoService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Date;

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

    @Value("${app.jwt.secret}")
    private String loginSecret;

    @Value("${app.jwt.expire_minutes}")
    private Integer expireMin;

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();


    /**
     * registerUser: verify input -> hash pwd -> save user -> generate activation token -> save activation token -> send email
     * @param userInfoRequest userinfo required: name, pwd, email; optional: birthdate
     * @return success msg or fail msg
     */
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

        return "We have received your register and sent an activation email to you. Please check your email. Thank you!";
    }

    /**
     * activateUser: check token -> check token use status -> check expired -> update user status -> update activate token use time
     * @param token token info
     * @return success msg or fail msg
     */
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

    /**
     * resend activation email: find user -> find token -> verify use status and expire time -> generate new activation token -> send new activation email
     * @param email user email
     * @return success or fail msg
     */
    @Override
    public String resendActEmail(String email) {
        // find user
        UserInfoEntity user = userInfoRepo.findByUserEmail(email);
        if(user == null){
            throw new IllegalArgumentException("Invalid User");
        }
        if(user.getUserStatus() != UserStatus.PENDING){
            throw new IllegalStateException("User already activated");
        }

        // find token
        UserActivationToken userActivationToken = userActivationTokenRepo.findByUserId(user.getUserId());
        if(userActivationToken == null){
            throw new IllegalArgumentException("Invalid UserToken");
        }
        // verify whether it has been used
        if(userActivationToken.getUsedAt() != null){
            throw new IllegalArgumentException("User already activated");
        }
        // verify whether it has been expired
        if(userActivationToken.getExpiresAt().isAfter(LocalDateTime.now())){
            return "We have sent you an activation email, Please check your email";
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
        }catch (Exception ex){
            ex.printStackTrace();
            throw ex;
        }

        return "We have sent you a new activation email, Please check your email";
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

       // verify the user
       UserInfoEntity user = userInfoRepo.findByUserEmail(userEmail);
       if(user == null){
           throw new IllegalArgumentException("Invalid login! Please check your account");
       }
       if(user.getUserStatus() != UserStatus.ACTIVE){
           throw new IllegalStateException("Invalid login! Account not activated");
       }
       if(!passwordEncoder.matches(userPwd, user.getUserPwdHash())){
           throw new IllegalArgumentException("Invalid login! Please check your account");
       }

       // generate login token
       String loginToken = generateLoginToken(user.getUserId(), user.getUserEmail());

       //return info
       return new LoginResponse(
                loginToken,
               "Bearer",
                user.getUserId(),
                user.getUserName(),
                user.getUserEmail()
        );
    }

    /**
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


    /**
     * generate login token
     * @param userId user id
     * @param userEmail user email
     * @return login token
     */
    private String generateLoginToken(Long userId, String userEmail){
        // generate key
        SecretKey key = Keys.hmacShaKeyFor(loginSecret.getBytes(StandardCharsets.UTF_8));
        // get the current time、expire time
        Instant now = Instant.now();
        Instant expireTime = now.plus(expireMin, ChronoUnit.MINUTES);
        //return
        return Jwts.builder()
                .subject(userEmail)
                .claim("userId", userId)
                .issuedAt(Date.from(now))
                .expiration(Date.from(expireTime))
                .signWith(key)
                .compact();
    }

}
