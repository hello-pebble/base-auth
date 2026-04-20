package com.pebble.matching.domain

import java.time.LocalDateTime
import java.util.UUID

/**
 * 매칭을 위한 프로필 설정 (노출 수락 여부)
 */
data class MatchingProfile(
    val userId: Long,
    val isExposed: Boolean = false,
    val updatedAt: LocalDateTime = LocalDateTime.now()
)

/**
 * 사용자가 부여한 순위 정보 (1~3위)
 */
data class MatchRanking(
    val fromUserId: Long,
    val toUserId: Long,
    val rank: Int,
    val createdAt: LocalDateTime = LocalDateTime.now()
) {
    init {
        require(rank in 1..3) { "순위는 1위에서 3위 사이여야 합니다." }
    }
}

/**
 * 상호 매칭 결과 및 대화방 정보
 */
data class ChatMatch(
    val id: String = UUID.randomUUID().toString(),
    val userA: Long,
    val userB: Long,
    val createdAt: LocalDateTime = LocalDateTime.now()
)

/**
 * 외부(인증 서비스 등)에서 가져올 사용자 정보 인터페이스
 */
interface UserProvider {
    fun getUserInfo(userId: Long): ExternalUser?
}

data class ExternalUser(val id: Long, val username: String)
