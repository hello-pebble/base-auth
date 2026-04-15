package com.pebble.basicAuth.config

import com.pebble.basicAuth.persistence.RefreshTokenRepository
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.core.Authentication
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler
import org.springframework.stereotype.Component
import java.io.IOException

@Component
class CustomAuthenticationHandler(
    private val refreshTokenRepository: RefreshTokenRepository
) : AuthenticationEntryPoint, LogoutSuccessHandler {

    @Throws(IOException::class)
    override fun commence(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authException: AuthenticationException
    ) {
        response.status = HttpServletResponse.SC_UNAUTHORIZED
        response.contentType = "application/json;charset=UTF-8"
        response.writer.write("{\"message\":\"인증이 필요합니다.\"}")
    }

    @Throws(IOException::class)
    override fun onLogoutSuccess(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authentication: Authentication?
    ) {
        // [Phase 2-2] 로그아웃 시 Redis에서 Refresh Token 삭제
        if (authentication != null && authentication.name != null) {
            refreshTokenRepository.deleteByUsername(authentication.name)
        }

        response.status = HttpServletResponse.SC_OK
        response.contentType = "application/json;charset=UTF-8"
        response.writer.write("{\"message\":\"로그아웃 되었습니다.\"}")
    }
}
