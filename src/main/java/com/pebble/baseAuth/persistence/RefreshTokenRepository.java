package com.pebble.baseAuth.persistence;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * [Phase 2-2] Redis를 이용한 Refresh Token 저장소
 * Access Token과 달리 상태 유지가 필요한 Refresh Token을 관리합니다.
 */
@Repository
@RequiredArgsConstructor
public class RefreshTokenRepository {

    private final StringRedisTemplate redisTemplate;
    private static final String PREFIX = "RT:"; // Refresh Token 키 접두사

    /**
     * Refresh Token 저장 (TTL 설정 포함)
     */
    public void save(String username, String refreshToken, long expirationMillis) {
        redisTemplate.opsForValue().set(
                PREFIX + username,
                refreshToken,
                expirationMillis,
                TimeUnit.MILLISECONDS
        );
    }

    /**
     * 사용자명으로 Refresh Token 조회
     */
    public Optional<String> findByUsername(String username) {
        String token = redisTemplate.opsForValue().get(PREFIX + username);
        return Optional.ofNullable(token);
    }

    /**
     * Refresh Token 삭제 (로그아웃 및 Rotation 시 사용)
     */
    public void deleteByUsername(String username) {
        redisTemplate.delete(PREFIX + username);
    }
}
