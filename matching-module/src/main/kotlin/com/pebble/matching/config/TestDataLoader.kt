package com.pebble.matching.config

import com.pebble.matching.domain.ChatMatch
import com.pebble.matching.domain.MatchingProfile
import com.pebble.matching.infrastructure.InMemoryMatchingStore
import org.springframework.boot.CommandLineRunner
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import java.time.LocalDateTime

@Configuration
@Profile("!test")
class TestDataLoader(
    private val store: InMemoryMatchingStore
) {

    @Bean
    fun initMatchingData() = CommandLineRunner {
        // 1. 프로필 노출 설정 (user: 1, admin: 2, samples: 100~102)
        val userIds = listOf(1L, 2L, 100L, 101L, 102L)
        userIds.forEach { id ->
            store.saveProfile(MatchingProfile(userId = id, isExposed = true))
        }

        // 2. 이미 성사된 매칭 데이터 주입 (user <-> sample100)
        val existingMatch = ChatMatch(
            id = "match_999", // task-module의 테스트 데이터와 ID 일치시킴
            userA = 1L,
            userB = 100L,
            createdAt = LocalDateTime.now().minusDays(1)
        )
        store.saveMatch(existingMatch)

        println(">>> Matching Module Test Data Initialized (In-memory)")
    }
}
