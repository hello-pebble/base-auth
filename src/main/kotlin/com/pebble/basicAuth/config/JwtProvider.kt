package com.pebble.basicAuth.config

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
        // [Phase 2-1] SecretKey ì´ˆê¸°??(HS256 ?Œê³ ë¦¬ì¦˜ ?¬ìš©)
        this.secretKey = Keys.hmacShaKeyFor(secretKeyString.toByteArray(StandardCharsets.UTF_8))
    }

    /**
     * Access Token ?ì„±
     * payload claim: sub(?¬ìš©?ëª…), roles(ê¶Œí•œ), iat(ë°œê¸‰?œê°„), exp(ë§Œë£Œ?œê°„)
     */
    fun createAccessToken(username: String, role: String): String {
        return createToken(username, mapOf("roles" to role), accessExpiration)
    }

    /**
     * Refresh Token ?ì„±
     * ë³´ì•ˆ???„í•´ ìµœì†Œ?œì˜ ?•ë³´(sub)ë§??¬í•¨
     */
    fun createRefreshToken(username: String): String {
        return createToken(username, emptyMap(), refreshExpiration)
    }

    private fun createToken(username: String, claims: Map<String, Any>, expiration: Long): String {
        val now = Date()
        val expiryDate = Date(now.time + expiration)

        return Jwts.builder()
            .subject(username)
            .claims(claims)
            .issuedAt(now)
            .expiration(expiryDate)
            .signWith(secretKey)
            .compact()
    }

    /**
     * ? í°?ì„œ Claims ì¶”ì¶œ
     */
    fun getClaims(token: String): Claims {
        return Jwts.parser()
            .verifyWith(secretKey)
            .build()
            .parseSignedClaims(token)
            .payload
    }

    /**
     * ? í° ? íš¨??ê²€ì¦?
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
