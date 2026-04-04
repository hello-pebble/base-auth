package com.pebble.baseAuth.persistence

import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Repository
import java.util.Optional
import java.util.concurrent.TimeUnit

/**
 * [Phase 2-2] Redis를 이용한 Refresh Token 저장소
 * Access Token과 달리 상태 유지가 필요한 Refresh Token을 관리합니다.
 */
@Repository
class RefreshTokenRepository(private val redisTemplate: StringRedisTemplate) {

    /**
     * Refresh Token 저장 (TTL 설정 포함)
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
     * 사용자명으로 Refresh Token 조회
     */
    fun findByUsername(username: String): Optional<String> {
        val token = redisTemplate.opsForValue().get(PREFIX + username)
        return Optional.ofNullable(token)
    }

    /**
     * Refresh Token 삭제 (로그아웃 및 Rotation 시 사용)
     */
    fun deleteByUsername(username: String) {
        redisTemplate.delete(PREFIX + username)
    }

    companion object {
        private const val PREFIX = "RT:" // Refresh Token 키 접두사
    }
}
