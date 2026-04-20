package com.pebble.matching.controller

import com.pebble.matching.domain.ChatMatch
import com.pebble.matching.domain.MatchResult
import com.pebble.matching.domain.MatchingService
import com.pebble.matching.domain.RecommendedUser
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/matching")
class MatchingController(
    private val matchingService: MatchingService
) {
    // 임시로 Principal의 name을 ID(Long)로 변환하여 사용하거나, 
    // 별도의 UserResolver를 통해 ID를 주입받는 방식을 가정합니다.
    private fun getUserId(authentication: Authentication): Long {
        // 실제 운영 시에는 JWT 클레임에서 ID를 추출하는 방식으로 구현
        return authentication.name.toLongOrNull() ?: 0L 
    }

    @GetMapping("/recommendations")
    fun getRecommendations(authentication: Authentication): ResponseEntity<List<RecommendedUser>> {
        return ResponseEntity.ok(matchingService.getRecommendations(getUserId(authentication)))
    }

    @PostMapping("/rank")
    fun rankUser(
        authentication: Authentication,
        @RequestBody request: RankRequest
    ): ResponseEntity<MatchResult> {
        return ResponseEntity.ok(matchingService.rankUser(getUserId(authentication), request.toUserId, request.rank))
    }

    @PutMapping("/exposure")
    fun updateExposure(
        authentication: Authentication,
        @RequestBody request: ExposureRequest
    ): ResponseEntity<String> {
        matchingService.updateExposure(getUserId(authentication), request.isExposed)
        return ResponseEntity.ok("매칭 노출 설정이 변경되었습니다.")
    }

    @GetMapping("/matches")
    fun getMyMatches(authentication: Authentication): ResponseEntity<List<ChatMatch>> {
        return ResponseEntity.ok(matchingService.getMyMatches(getUserId(authentication)))
    }

    data class RankRequest(val toUserId: Long, val rank: Int)
    data class ExposureRequest(val isExposed: Boolean)
}
