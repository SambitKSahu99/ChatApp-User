package com.elixr.ChatApp_UserManagement.filter;

import com.elixr.ChatApp_UserManagement.contants.MessagesConstants;
import com.elixr.ChatApp_UserManagement.contants.UserConstants;
import com.elixr.ChatApp_UserManagement.response.Response;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        Response customErrorResponse = new Response();
        ObjectMapper objectMapper = new ObjectMapper();
        String errorMessage = authException.getMessage();
        if (errorMessage.contains(UserConstants.CLIENT_ERROR)) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            customErrorResponse.setResponse(UserConstants.CLIENT_ERROR);
            log.error(UserConstants.CLIENT_ERROR,authException);
        } else if (errorMessage.contains(UserConstants.SERVER_ERROR)) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            customErrorResponse.setResponse(UserConstants.SERVER_ERROR);
            log.error(UserConstants.SERVER_ERROR,authException);
        } else {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            customErrorResponse.setResponse(MessagesConstants.ACCESS_DENIED);
            log.error(MessagesConstants.ERROR_OCCURRED,authException);
        }
        response.getWriter().write(objectMapper.writeValueAsString(customErrorResponse));

    }
}
