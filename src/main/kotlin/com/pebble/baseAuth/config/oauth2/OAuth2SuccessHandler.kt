package com.pebble.baseAuth.config.oauth2

import com.pebble.baseAuth.config.JwtProvider
import com.pebble.baseAuth.persistence.RefreshTokenRepository
import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.core.Authentication
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler
import org.springframework.stereotype.Component
import java.io.IOException

@Component
class OAuth2SuccessHandler(
    private val jwtProvider: JwtProvider,
    private val refreshTokenRepository: RefreshTokenRepository
) : SimpleUrlAuthenticationSuccessHandler() {

    @Throws(IOException::class)
    override fun onAuthenticationSuccess(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authentication: Authentication
    ) {
        val oAuth2User = authentication.principal as CustomOAuth2User
        val username = oAuth2User.user.username
        val role = oAuth2User.user.role.name

        val accessToken = jwtProvider.createAccessToken(username, role)
        val refreshToken = jwtProvider.createRefreshToken(username)

        // Redis에 Refresh Token 저장
        refreshTokenRepository.save(username, refreshToken, jwtProvider.refreshExpiration)

        // [CTO 리뷰 반영] 쿠키 만료 시간을 JwtProvider 설정과 동기화
        addCookie(response, "accessToken", accessToken, (jwtProvider.accessExpiration / 1000).toInt())
        addCookie(response, "refreshToken", refreshToken, (jwtProvider.refreshExpiration / 1000).toInt())

        // 메인 페이지로 리다이렉트
        redirectStrategy.sendRedirect(request, response, "/")
    }

    private fun addCookie(response: HttpServletResponse, name: String, value: String, maxAge: Int) {
        val cookie = Cookie(name, value).apply {
            path = "/"
            isHttpOnly = true
            secure = true // HTTPS가 아니면 로컬 테스트 시 주의 필요
            this.maxAge = maxAge
        }
        response.addCookie(cookie)
    }
}
