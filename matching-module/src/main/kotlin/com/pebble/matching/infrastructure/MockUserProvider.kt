package com.pebble.matching.infrastructure

import com.pebble.matching.domain.ExternalUser
import com.pebble.matching.domain.UserProvider
import org.springframework.stereotype.Component

@Component
class MockUserProvider : UserProvider {
    /**
     * 실제 운영 시에는 Auth 서버의 API를 호출하거나 
     * DB에서 공통으로 조회하는 방식으로 대체 가능
     */
    override fun getUserInfo(userId: Long): ExternalUser? {
        return when (userId) {
            1L -> ExternalUser(1L, "user1")
            2L -> ExternalUser(2L, "user2")
            3L -> ExternalUser(3L, "user3")
            else -> ExternalUser(userId, "external_user_$userId")
        }
    }
}
