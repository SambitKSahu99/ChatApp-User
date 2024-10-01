package com.elixr.ChatApp_UserManagement.filter;

import com.elixr.ChatApp_UserManagement.contants.LogInfoConstants;
import com.elixr.ChatApp_UserManagement.contants.UrlConstants;
import com.elixr.ChatApp_UserManagement.contants.UserConstants;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.Collections;

@Slf4j
@Component
public class JwtFilter extends OncePerRequestFilter {

    private final WebClient webClient;
    @Getter
    private String currentUserName;
    @Getter
    private String currentToken;
    @Value(UserConstants.AUTH_SERVICE_URL_VALUE)
    private String authServiceUrl;

    public JwtFilter(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {
        String requestPath = request.getServletPath();
        if (UrlConstants.REGISTER_USER_API_ENDPOINT.equals(requestPath)) {
            filterChain.doFilter(request, response);
            return;
        }
        try {
            String token = extractToken(request);
            currentToken = token;
            if (StringUtils.hasText(token)) {
                String userName = webClient.post()
                        .uri(authServiceUrl+UrlConstants.VERIFY_TOKEN_ENDPOINT)
                        .header(UserConstants.AUTHORIZATION_HEADER
                                ,UserConstants.BEARER+token)
                        .retrieve()
                        .onStatus(HttpStatusCode::is4xxClientError, clientResponse ->
                                clientResponse.bodyToMono(String.class)
                                        .flatMap(errorMessage -> Mono.error(new RuntimeException(UserConstants.CLIENT_ERROR + errorMessage))))
                        .onStatus(HttpStatusCode::is5xxServerError, clientResponse ->
                                clientResponse.bodyToMono(String.class)
                                        .flatMap(errorMessage -> Mono.error(new RuntimeException(UserConstants.SERVER_ERROR + errorMessage))))
                        .bodyToMono(String.class)
                        .block();
                if (userName != null) {
                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(userName, null, Collections.emptyList());
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    log.info(LogInfoConstants.TOKEN_VERIFIED);
                    currentUserName = userName;
                    MDC.put("user", currentUserName);
                }
            }
        } catch (RuntimeException exception) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }
        filterChain.doFilter(request, response);
    }

    private String extractToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(UserConstants.AUTHORIZATION_HEADER);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(UserConstants.BEARER)) {
            return bearerToken.substring(7);
        }
        return null;
    }
}

