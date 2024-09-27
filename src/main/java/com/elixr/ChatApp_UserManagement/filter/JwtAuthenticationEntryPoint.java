package com.elixr.ChatApp_UserManagement.filter;

import com.elixr.ChatApp_UserManagement.contants.MessagesConstants;
import com.elixr.ChatApp_UserManagement.contants.UserConstants;
import com.elixr.ChatApp_UserManagement.response.Response;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        Response customErrorResponse = new Response();
        ObjectMapper objectMapper = new ObjectMapper();
        if (authException.getMessage().contains(UserConstants.EXPIRED) || authException.getMessage().contains(UserConstants.INVALID)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            customErrorResponse.setResponse(MessagesConstants.INVALID_TOKEN);
            response.getWriter().write(objectMapper.writeValueAsString(customErrorResponse));
        } else {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            customErrorResponse.setResponse(MessagesConstants.ACCESS_DENIED);
            response.getWriter().write(objectMapper.writeValueAsString(customErrorResponse));
        }
    }
}
