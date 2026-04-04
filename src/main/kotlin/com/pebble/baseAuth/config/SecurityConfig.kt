package com.pebble.baseAuth.config

import com.pebble.baseAuth.config.oauth2.CustomOAuth2UserService
import com.pebble.baseAuth.config.oauth2.OAuth2SuccessHandler
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

@Configuration
/**
 * [권한 시스템 도입 - Step 3]
 * [Phase 2-3] Stateless 인증 체계로 전환합니다.
 */
@EnableMethodSecurity
class SecurityConfig(
    private val authenticationHandler: CustomAuthenticationHandler,
    private val jwtAuthenticationFilter: JwtAuthenticationFilter,
    private val customOAuth2UserService: CustomOAuth2UserService,
    private val oAuth2SuccessHandler: OAuth2SuccessHandler
) {

    @Bean
    @Throws(Exception::class)
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            // [Phase 2-3] REST API 기반이므로 CSRF 및 기본 로그인 폼은 사용하지 않습니다.
            .csrf { it.disable() }
            .formLogin { it.disable() }
            .httpBasic { it.disable() }

            // [Phase 2-3] 모든 세션 관리를 Stateless로 설정 (JSESSIONID 생성 방지)
            .sessionManagement { session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            }

            .authorizeHttpRequests { auth ->
                auth.requestMatchers("/api/v1/users/signup", "/api/v1/login", "/api/v1/refresh").permitAll()
                    .requestMatchers("/h2-console/**").permitAll()
                    .anyRequest().authenticated()
            }

            // [Phase 2-3] JWT 필터를 UsernamePasswordAuthenticationFilter 앞에 배치
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter::class.java)

            .oauth2Login { oauth2 ->
                oauth2.userInfoEndpoint { userInfo -> userInfo.userService(customOAuth2UserService) }
                    .successHandler(oAuth2SuccessHandler)
            }

            .exceptionHandling { exception ->
                exception.authenticationEntryPoint(authenticationHandler)
            }
            .logout { logout ->
                logout.logoutUrl("/api/v1/logout")
                    .logoutSuccessHandler(authenticationHandler)
            }
            .headers { headers -> headers.frameOptions { it.disable() } }

        return http.build()
    }

    @Bean
    @Throws(Exception::class)
    fun authenticationManager(authenticationConfiguration: AuthenticationConfiguration): AuthenticationManager {
        return authenticationConfiguration.authenticationManager
    }
}
