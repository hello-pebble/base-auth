package com.pebble.matching.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain

@Configuration
class MatchingSecurityConfig {

    @Bean
    fun matchingSecurityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .csrf { it.disable() }
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .authorizeHttpRequests { auth ->
                // 실제 운영 시에는 JWT 필터를 통과한 인증 정보가 필요하지만, 
                // 지금은 로직 검증을 위해 permitAll()로 시작하겠습니다.
                auth.requestMatchers("/api/v1/matching/**").permitAll()
                    .anyRequest().authenticated()
            }
        
        return http.build()
    }
}
