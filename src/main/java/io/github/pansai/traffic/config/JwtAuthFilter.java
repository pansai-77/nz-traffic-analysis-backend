package io.github.pansai.traffic.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.pansai.traffic.enums.ErrorCode;
import io.github.pansai.traffic.handler.ApiResponse;
import io.github.pansai.traffic.service.JwtAuthService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.security.SignatureException;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    @Autowired
    private JwtAuthService jwtAuthService;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private ObjectMapper objectMapper;

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

        //get subject - email
        Claims claims = null;
        try {
            // get claims
            claims = jwtAuthService.resolveLoginToken(loginToken);

            // find user, verify token
            if(claims != null && claims.getSubject() != null && !claims.getSubject().isBlank() && SecurityContextHolder.getContext().getAuthentication() == null){
                UserDetails userDetails = userDetailsService.loadUserByUsername(claims.getSubject());
                if(!userDetails.isEnabled()) {
                    writeApiError(response, ErrorCode.USER_LOGIN_NOT_ACTIVATE);
                    return;
                }

                if(jwtAuthService.validLoginToken(claims, userDetails)){
                    UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities()
                    );
                    authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                }
            }
        } catch (ExpiredJwtException ex) {
            writeApiError(response, ErrorCode.USER_AUTH_TOKEN_EXPIRED);
            return;
        } catch (JwtException | IllegalArgumentException ex) {
            writeApiError(response, ErrorCode.USER_AUTH_TOKEN_INVALID);
            return;
        } catch (UsernameNotFoundException ex) {
            writeApiError(response, ErrorCode.USER_AUTH_USER_NOT_EXISTS_ERR);
            return;
        } catch (Exception ex) {
            writeApiError(response, ErrorCode.USER_AUTH_TOKEN_VALID_ERROR);
            return;
        }

        filterChain.doFilter(request, response);
    }

    /**
     * conversion return
     * @param response response
     * @param code code
     */
    private void writeApiError(HttpServletResponse response, ErrorCode code) throws IOException {
        if (response.isCommitted()) {
            return;
        }

        response.setStatus(code.httpStatus().value());
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=UTF-8");

        ApiResponse<Void> body = ApiResponse.fail(code);
        body.setTraceId(MDC.get("traceId"));

        response.getWriter().write(objectMapper.writeValueAsString(body));
    }

}
