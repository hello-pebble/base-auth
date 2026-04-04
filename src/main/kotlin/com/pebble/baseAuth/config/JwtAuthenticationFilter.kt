package com.pebble.baseAuth.config

import io.jsonwebtoken.Claims
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletException
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpHeaders
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Component
import org.springframework.util.StringUtils
import org.springframework.web.filter.OncePerRequestFilter
import java.io.IOException

/**
 * [Phase 2-3] JWT 인증 필터
 * 모든 요청에서 JWT 토큰을 추출하고 검증하여 SecurityContext에 인증 정보를 설정합니다.
 */
@Component
class JwtAuthenticationFilter(private val jwtProvider: JwtProvider) : OncePerRequestFilter() {

    @Throws(ServletException::class, IOException::class)
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        // 1. Request Header에서 JWT 토큰 추출
        val token = resolveToken(request)

        // 2. 토큰 유효성 검사
        if (token != null && jwtProvider.validateToken(token)) {
            // 3. 토큰이 유효하면 인증 객체 생성
            val authentication = getAuthentication(token)
            // 4. SecurityContext에 인증 객체 저장
            SecurityContextHolder.getContext().authentication = authentication
        }

        filterChain.doFilter(request, response)
    }

    /**
     * Authorization 헤더에서 "Bearer " 접두사를 제외한 토큰 값 추출
     */
    private fun resolveToken(request: HttpServletRequest): String? {
        val bearerToken = request.getHeader(HttpHeaders.AUTHORIZATION)
        return if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            bearerToken.substring(7)
        } else {
            null
        }
    }

    /**
     * 토큰에 담긴 정보를 기반으로 Authentication 객체 생성
     * [중요] Stateless 구조이므로 매번 DB를 조회하지 않고 토큰의 Claims만 사용합니다.
     */
    private fun getAuthentication(token: String): Authentication {
        val claims: Claims = jwtProvider.getClaims(token)
        val username = claims.subject
        val role = claims.get("roles", String::class.java)

        val authorities = listOf(SimpleGrantedAuthority(role))

        // Spring Security 내부에서 사용할 UserDetails 객체 생성
        val principal: UserDetails = User(username, "", authorities)

        return UsernamePasswordAuthenticationToken(principal, token, authorities)
    }
}
