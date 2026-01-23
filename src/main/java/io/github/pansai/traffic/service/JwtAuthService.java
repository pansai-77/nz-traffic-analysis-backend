package io.github.pansai.traffic.service;

import io.jsonwebtoken.Claims;
import org.springframework.security.core.userdetails.UserDetails;

public interface JwtAuthService {

    /**
     * generate login token
     * @param userId user id
     * @param userEmail user email
     * @return success or fail msg
     */
    String generateLoginToken(Long userId, String userEmail);

    /**
     * resolve login token get user email
     * @param loginToken token
     * @return subject-email
     */
    Claims resolveLoginToken(String loginToken);


    /**
     * valid login token
     * @return
     */
    boolean validLoginToken(Claims claims, UserDetails userDetails);
}
