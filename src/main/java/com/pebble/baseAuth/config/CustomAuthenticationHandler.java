package com.pebble.baseAuth.config;

import com.pebble.baseAuth.persistence.RefreshTokenRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class CustomAuthenticationHandler implements AuthenticationEntryPoint, LogoutSuccessHandler {

    private final RefreshTokenRepository refreshTokenRepository;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write("{\"message\":\"인증이 필요합니다.\"}");
    }

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response,
                                Authentication authentication) throws IOException {
        
        // [Phase 2-2] 로그아웃 시 Redis에서 Refresh Token 삭제
        if (authentication != null && authentication.getName() != null) {
            refreshTokenRepository.deleteByUsername(authentication.getName());
        }

        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write("{\"message\":\"로그아웃 되었습니다.\"}");
    }
}
