package com.elixr.ChatApp_UserManagement.filter;

import com.elixr.ChatApp_UserManagement.contants.LogInfoConstants;
import com.elixr.ChatApp_UserManagement.contants.UserConstants;
import com.elixr.ChatApp_UserManagement.service.JwtService;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Slf4j
@Component
public class JwtFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    @Getter
    private String currentUser;

    public JwtFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws IOException {
        String authHeader = request.getHeader(UserConstants.AUTHORIZATION_HEADER);
        String token = null;
        String userName = null;
        try {
            if (authHeader != null && authHeader.startsWith(UserConstants.BEARER)) {
                token = authHeader.substring(7);
                userName = jwtService.extractUserName(token);
            }
            if (userName != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                if (jwtService.validateToken(token)) {
                    log.info(LogInfoConstants.Token_IS_VALID);
                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(userName, null, Collections.emptyList());
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                    MDC.put("userName",userName);
                }
                currentUser = userName;
            }
            filterChain.doFilter(request,response);
        }catch(JwtException | IllegalArgumentException exception){
            log.error(LogInfoConstants.INVALID_TOKEN + "{}", exception.getMessage());
            MDC.clear();
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED,LogInfoConstants.INVALID_TOKEN+exception.getMessage());
        }catch (Exception exception){
            log.error(LogInfoConstants.SOME_ERROR_OCCURRED + "{}", exception.getMessage());
            MDC.clear();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,LogInfoConstants.SOME_ERROR_OCCURRED+exception.getMessage());
        }
    }
}

