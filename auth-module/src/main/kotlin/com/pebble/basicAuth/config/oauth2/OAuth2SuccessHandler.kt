package com.pebble.basicAuth.config.oauth2

import com.pebble.basicAuth.config.JwtProvider
import com.pebble.basicAuth.persistence.RefreshTokenRepository
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

        // Redis??Refresh Token ?�??
        refreshTokenRepository.save(username, refreshToken, jwtProvider.refreshExpiration)

        // [CTO 리뷰 반영] 쿠키 만료 ?�간??JwtProvider ?�정�??�기??
        addCookie(response, "accessToken", accessToken, (jwtProvider.accessExpiration / 1000).toInt())
        addCookie(response, "refreshToken", refreshToken, (jwtProvider.refreshExpiration / 1000).toInt())

        // 메인 ?�이지�?리다?�렉??
        redirectStrategy.sendRedirect(request, response, "/")
    }

    private fun addCookie(response: HttpServletResponse, name: String, value: String, maxAge: Int) {
        val cookie = Cookie(name, value).apply {
            path = "/"
            isHttpOnly = true
            secure = true // HTTPS가 ?�니�?로컬 ?�스????주의 ?�요
            this.maxAge = maxAge
        }
        response.addCookie(cookie)
    }
}
