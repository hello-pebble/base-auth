package com.pebble.basicAuth.controller

import com.pebble.basicAuth.config.WaitingRoomService
import org.springframework.web.bind.annotation.*

/**
 * [Phase 4: Waiting Room Polling API]
 * ?¬ìš©?ê? ?ì‹ ???€ê¸??œì„œë¥??•ì¸?˜ê³  ì§„ì… ê°€???¬ë?ë¥?ì²´í¬?˜ëŠ” ?¸í„°?˜ì´?¤ì…?ˆë‹¤.
 */
@RestController
@RequestMapping("/api/v1/waiting-room")
class WaitingRoomController(
    private val waitingRoomService: WaitingRoomService
) {

    /**
     * ?„ì¬ ?˜ì˜ ?€ê¸??íƒœ?€ ?œë²ˆ???•ì¸?©ë‹ˆ??
     * ?´ë¼?´ì–¸?¸ëŠ” ??APIë¥?ì£¼ê¸°?ìœ¼ë¡??¸ì¶œ(Polling)?˜ì—¬ 'ALLOWED' ?íƒœê°€ ???Œê¹Œì§€ ê¸°ë‹¤ë¦½ë‹ˆ??
     */
    @GetMapping("/status")
    fun getStatus(@RequestParam userId: String): WaitingRoomService.WaitingStatus {
        return waitingRoomService.register(userId)
    }
}
