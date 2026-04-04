package com.pebble.baseAuth.config

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import jakarta.annotation.PostConstruct
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import javax.crypto.SecretKey
import java.nio.charset.StandardCharsets
import java.util.*

@Component
class JwtProvider {

    @Value("\${jwt.secret}")
    private lateinit var secretKeyString: String

    @Value("\${jwt.access-expiration}")
    val accessExpiration: Long = 0

    @Value("\${jwt.refresh-expiration}")
    val refreshExpiration: Long = 0

    private lateinit var secretKey: SecretKey

    @PostConstruct
    protected fun init() {
        // [Phase 2-1] SecretKey 초기화 (HS256 알고리즘 사용)
        this.secretKey = Keys.hmacShaKeyFor(secretKeyString.toByteArray(StandardCharsets.UTF_8))
    }

    /**
     * Access Token 생성
     * payload claim: sub(사용자명), roles(권한), iat(발급시간), exp(만료시간)
     */
    fun createAccessToken(username: String, role: String): String {
        val now = Date()
        val expiryDate = Date(now.time + accessExpiration)

        return Jwts.builder()
            .subject(username)
            .claim("roles", role)
            .issuedAt(now)
            .expiration(expiryDate)
            .signWith(secretKey)
            .compact()
    }

    /**
     * Refresh Token 생성
     * 보안을 위해 최소한의 정보(sub)만 포함
     */
    fun createRefreshToken(username: String): String {
        val now = Date()
        val expiryDate = Date(now.time + refreshExpiration)

        return Jwts.builder()
            .subject(username)
            .issuedAt(now)
            .expiration(expiryDate)
            .signWith(secretKey)
            .compact()
    }

    /**
     * 토큰에서 Claims 추출
     */
    fun getClaims(token: String): Claims {
        return Jwts.parser()
            .verifyWith(secretKey)
            .build()
            .parseSignedClaims(token)
            .payload
    }

    /**
     * 토큰 유효성 검증
     */
    fun validateToken(token: String): Boolean {
        return try {
            Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token)
            true
        } catch (e: Exception) {
            false
        }
    }
}
