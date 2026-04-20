package com.pebble.basicAuth.config

import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Service

/**
 * [Phase 4: Virtual Waiting Room Service]
 * 대량의 유저를 Redis Sorted Set에 줄 세우고, 순차적으로 진입을 허용하는 핵심 엔진입니다.
 */
@Service
class WaitingRoomService(
    private val redisTemplate: StringRedisTemplate
) {
    private val WAITING_QUEUE_KEY = "waiting_room:queue"
    private val PROCEED_USER_KEY = "waiting_room:proceed"
    private val CAPACITY_THRESHOLD = 100 // 서버가 1초당 처리 가능한 최대 요청 수

    /**
     * 유저를 대기열에 등록하고 현재 상태를 반환합니다.
     */
    fun register(userId: String): WaitingStatus {
        val now = System.currentTimeMillis().toDouble()
        
        // 1. 이미 진입 허용된 유저인지 확인
        if (isUserAllowed(userId)) {
            return WaitingStatus(status = "ALLOWED", rank = 0)
        }

        // 2. 대기열에 등록 (이미 있으면 순서 유지)
        redisTemplate.opsForZSet().add(WAITING_QUEUE_KEY, userId, now)
        
        // 3. 현재 순번 계산 (0부터 시작하므로 +1)
        val rank = redisTemplate.opsForZSet().rank(WAITING_QUEUE_KEY, userId) ?: 0L
        
        return WaitingStatus(status = "WAITING", rank = rank + 1)
    }

    /**
     * 유저가 실제 로직을 수행할 수 있는 상태인지 확인합니다.
     */
    fun isUserAllowed(userId: String): Boolean {
        return redisTemplate.opsForSet().isMember(PROCEED_USER_KEY, userId) == true
    }

    /**
     * [스케줄러에서 호출] 대기열의 유저를 일정 단위(Batch)로 진입 허용 상태로 전환합니다.
     */
    fun allowUserBatch() {
        // 대기열 앞쪽에서 CAPACITY_THRESHOLD 만큼 유저 추출
        val users = redisTemplate.opsForZSet().range(WAITING_QUEUE_KEY, 0, (CAPACITY_THRESHOLD - 1).toLong())
        
        if (!users.isNullOrEmpty()) {
            // 1. 진입 허용 목록에 추가 (Redisson 기반 분산 락 없이도 Redis 원자적 연산으로 처리 가능)
            redisTemplate.opsForSet().add(PROCEED_USER_KEY, *users.toTypedArray())
            // 2. 대기열에서 제거
            redisTemplate.opsForZSet().remove(WAITING_QUEUE_KEY, *users.toTypedArray())
        }
    }

    /**
     * 실제 로직 수행 완료 후 진입 허용 목록에서 제거합니다.
     */
    fun removeFromProceed(userId: String) {
        redisTemplate.opsForSet().remove(PROCEED_USER_KEY, userId)
    }

    data class WaitingStatus(val status: String, val rank: Long)
}
