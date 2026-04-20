package com.pebble.basicAuth.persistence

import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Repository
import java.util.Optional
import java.util.concurrent.TimeUnit

/**
 * [Phase 2-2] Redisë¥??´ìš©??Refresh Token ?€?¥ì†Œ
 * Access Tokenê³??¬ë¦¬ ?íƒœ ? ì?ê°€ ?„ìš”??Refresh Token??ê´€ë¦¬í•©?ˆë‹¤.
 */
@Repository
class RefreshTokenRepository(private val redisTemplate: StringRedisTemplate) {

    /**
     * Refresh Token ?€??(TTL ?¤ì • ?¬í•¨)
     */
    fun save(username: String, refreshToken: String, expirationMillis: Long) {
        redisTemplate.opsForValue().set(
            PREFIX + username,
            refreshToken,
            expirationMillis,
            TimeUnit.MILLISECONDS
        )
    }

    /**
     * ?¬ìš©?ëª…?¼ë¡œ Refresh Token ì¡°íšŒ
     */
    fun findByUsername(username: String): Optional<String> {
        val token = redisTemplate.opsForValue().get(PREFIX + username)
        return Optional.ofNullable(token)
    }

    /**
     * Refresh Token ?? œ (ë¡œê·¸?„ì›ƒ ë°?Rotation ???¬ìš©)
     */
    fun deleteByUsername(username: String) {
        redisTemplate.delete(PREFIX + username)
    }

    companion object {
        private const val PREFIX = "RT:" // Refresh Token ???‘ë‘??
    }
}
