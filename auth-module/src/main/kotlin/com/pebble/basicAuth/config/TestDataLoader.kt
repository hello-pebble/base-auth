package com.pebble.basicAuth.config

import com.pebble.basicAuth.domain.UserRole
import com.pebble.basicAuth.domain.UserService
import com.pebble.basicAuth.domain.UserRepository
import com.pebble.basicAuth.domain.User
import org.springframework.boot.CommandLineRunner
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.security.crypto.password.PasswordEncoder

@Configuration
@Profile("!test") // 테스트 환경이 아닐 때만 실행 (local, dev 등)
class TestDataLoader(
    private val userService: UserService,
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder
) {

    @Bean
    fun initData() = CommandLineRunner {
        // 1. 일반 사용자 생성 (없을 경우에만)
        if (!userRepository.existsByUsernameAndDeletedAtIsNull("user")) {
            userService.signUp("user", "user@example.com", "password123")
            println(">>> Test User Created: user / password123")
        }

        // 2. 관리자 사용자 생성 (없을 경우에만)
        if (!userRepository.existsByUsernameAndDeletedAtIsNull("admin")) {
            val encodedPassword = passwordEncoder.encode("password123")
            userRepository.save(User(
                id = null,
                username = "admin",
                email = "admin@example.com",
                password = encodedPassword,
                provider = null,
                providerId = null,
                role = UserRole.ROLE_ADMIN,
                deletedAt = null
            ))
            println(">>> Test Admin Created: admin / password123")
        }
    }
}
