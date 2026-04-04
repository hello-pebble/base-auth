package com.pebble.baseAuth.controller

import com.pebble.baseAuth.config.WaitingRoomService
import org.springframework.web.bind.annotation.*

/**
 * [Phase 4: Waiting Room Polling API]
 * 사용자가 자신의 대기 순서를 확인하고 진입 가능 여부를 체크하는 인터페이스입니다.
 */
@RestController
@RequestMapping("/api/v1/waiting-room")
class WaitingRoomController(
    private val waitingRoomService: WaitingRoomService
) {

    /**
     * 현재 나의 대기 상태와 순번을 확인합니다.
     * 클라이언트는 이 API를 주기적으로 호출(Polling)하여 'ALLOWED' 상태가 될 때까지 기다립니다.
     */
    @GetMapping("/status")
    fun getStatus(@RequestParam userId: String): WaitingRoomService.WaitingStatus {
        return waitingRoomService.register(userId)
    }
}
