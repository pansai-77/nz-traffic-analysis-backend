package io.github.pansai.traffic.config;

import io.github.pansai.traffic.service.JwtAuthService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    @Autowired
    private JwtAuthService jwtAuthService;

    @Autowired
    private UserDetailsService userDetailsService;

    @Value("${app.jwt.type}")
    private String jwtType;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        //get header authorization
        String headerAuth = request.getHeader(HttpHeaders.AUTHORIZATION);
        if(headerAuth == null || ! headerAuth.startsWith(jwtType)){
            filterChain.doFilter(request, response);
            return;
        }

        //get login token
        String loginToken = headerAuth.substring(jwtType.length());

        String subject = null;
        try {
            subject = jwtAuthService.resolveLoginToken(loginToken);
        } catch (Exception ex){
            filterChain.doFilter(request, response);
            return;
        }

        // find user, verify token
        if(subject != null && SecurityContextHolder.getContext().getAuthentication() == null){
            UserDetails userDetails = userDetailsService.loadUserByUsername(subject);

            if(jwtAuthService.validLoginToken(loginToken, userDetails)){
                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities()
                );
                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
        }
        filterChain.doFilter(request, response);
    }
}
