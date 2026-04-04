package com.pebble.baseAuth.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder

@Configuration
class AuthConfig {

    @Bean
    fun passwordEncoder(): PasswordEncoder {
        // [CTO Decision] Argon2의 유지보수 중단 및 높은 리소스 소모로 인해
        // 대중적이고 지속적으로 관리되는 BCrypt로 전환합니다.
        return BCryptPasswordEncoder()
    }
}
