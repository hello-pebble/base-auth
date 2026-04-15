package com.pebble.basicAuth.config

import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

/**
 * [Phase 4: Traffic Shaping Scheduler]
 * 대기열 유저를 일정 주기로 통과시켜 서버 부하를 일정하게 유지(Smoothing)합니다.
 */
@Component
@EnableScheduling
class WaitingRoomScheduler(
    private val waitingRoomService: WaitingRoomService
) {

    /**
     * 1초마다 실행하며, 서버가 처리 가능한 만큼의 유저를 대기열에서 꺼내 '진입 허용' 상태로 만듭니다.
     */
    @Scheduled(fixedDelay = 1000)
    fun processWaitingQueue() {
        waitingRoomService.allowUserBatch()
    }
}
