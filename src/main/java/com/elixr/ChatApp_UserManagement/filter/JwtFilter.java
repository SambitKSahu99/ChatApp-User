package com.elixr.ChatApp_UserManagement.filter;

import com.elixr.ChatApp_UserManagement.contants.UrlConstants;
import com.elixr.ChatApp_UserManagement.contants.UserConstants;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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

@Component
public class JwtFilter extends OncePerRequestFilter {

    private final WebClient webClient;
    private String currentUserName;
    private String currentToken;

    public JwtFilter(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl(UrlConstants.AUTH_SERVICE_URL).build();
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {
        try {
            String token = extractToken(request);
            currentToken = token;
            if (StringUtils.hasText(token)) {
                String userName = webClient.post()
                        .uri(UrlConstants.VERIFY_TOKEN_ENDPOINT)
                        .header(UserConstants.AUTHORIZATION_HEADER
                                , UserConstants.BEARER + token)
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
                    currentUserName = userName;
                }
            }
            filterChain.doFilter(request, response);
        } catch (RuntimeException exception) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
        }
    }

    private String extractToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(UserConstants.AUTHORIZATION_HEADER);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(UserConstants.BEARER)) {
            return bearerToken.substring(7);
        }
        return null;
    }

    public String getUserName() {
        return currentUserName;
    }

    public String getToken() {
        return currentToken;
    }
}

