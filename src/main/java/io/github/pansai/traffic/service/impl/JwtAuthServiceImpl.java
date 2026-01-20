package io.github.pansai.traffic.service.impl;

import io.github.pansai.traffic.service.JwtAuthService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.SecureRandom;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@Service("jwtAuthService")
public class JwtAuthServiceImpl implements JwtAuthService {

    @Value("${app.jwt.secret}")
    private String loginSecret;

    @Value("${app.jwt.expire_minutes}")
    private Integer expireMin;

    /**
     * generate login token
     * @param userId user id
     * @param userEmail user email
     * @return token
     */
    @Override
    public String generateLoginToken(Long userId, String userEmail) {
        // generate key
        SecretKey key = Keys.hmacShaKeyFor(loginSecret.getBytes(StandardCharsets.UTF_8));
        // get the current time、expire time
        Instant now = Instant.now();
        Instant expireTime = now.plus(expireMin, ChronoUnit.MINUTES);

        return Jwts.builder()
                // set info
                .subject(userEmail)
                .claim("userId", userId)
                .issuedAt(Date.from(now))
                .expiration(Date.from(expireTime))
                // sign with key
                .signWith(key)
                // generate token
                .compact();
    }

    /**
     * resolve login token get user email
     * @param loginToken user token
     * @return subject-email
     */
    @Override
    public String resolveLoginToken(String loginToken) {
        Claims claims = resolveClaims(loginToken);
        return claims.getSubject();
    }

    @Override
    public boolean validLoginToken(String loginToken, UserDetails userDetails) {
        // get email and expirationTime
        Claims claims = resolveClaims(loginToken);
        String emailSubject = claims.getSubject();
        Date expirationTime = claims.getExpiration();

        //verify user and whether expired
        boolean userVerify = emailSubject.equals(userDetails.getUsername());
        boolean tokenExpiredVerify = expirationTime.before(new Date());

        return userVerify && tokenExpiredVerify;
    }

    /**
     * get claims
     * @param loginToken user login token
     * @return claims
     */
    private Claims resolveClaims(String loginToken){
        SecretKey key = Keys.hmacShaKeyFor(loginSecret.getBytes(StandardCharsets.UTF_8));

        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(loginToken)
                .getPayload();
    }


}
