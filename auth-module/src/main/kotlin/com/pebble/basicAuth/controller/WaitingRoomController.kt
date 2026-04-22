package com.pebble.basicAuth.controller

import com.pebble.basicAuth.config.WaitingRoomService
import org.springframework.web.bind.annotation.*

/**
 * [Phase 4: Waiting Room Polling API]
 * ?�용?��? ?�신???��??�서�??�인?�고 진입 가???��?�?체크?�는 ?�터?�이?�입?�다.
 */
@RestController
@RequestMapping("/api/v1/waiting-room")
class WaitingRoomController(
    private val waitingRoomService: WaitingRoomService
) {

    /**
     * ?�재 ?�의 ?��??�태?� ?�번???�인?�니??
     * ?�라?�언?�는 ??API�?주기?�으�??�출(Polling)?�여 'ALLOWED' ?�태가 ???�까지 기다립니??
     */
    @GetMapping("/status")
    fun getStatus(
        @RequestParam userId: String,
        @RequestParam(defaultValue = "matching-service") serviceId: String
    ): WaitingRoomService.WaitingStatus {
        val isAllowed = waitingRoomService.isUserAllowed(userId, serviceId)
        val status = if (isAllowed) "ALLOWED" else "WAITING"
        return WaitingRoomService.WaitingStatus(status, 0)
    }
}
