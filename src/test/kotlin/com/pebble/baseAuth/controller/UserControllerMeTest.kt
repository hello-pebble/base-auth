package com.pebble.baseAuth.controller

import com.pebble.baseAuth.config.JwtProvider
import com.pebble.baseAuth.domain.UserService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.http.HttpHeaders
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.transaction.annotation.Transactional

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class UserControllerMeTest @Autowired constructor(
    private val mockMvc: MockMvc,
    private val userService: UserService,
    private val jwtProvider: JwtProvider
) {

    @MockitoBean
    private lateinit var clientRegistrationRepository: ClientRegistrationRepository

    @MockitoBean
    private lateinit var redisTemplate: StringRedisTemplate

    private val username = "testuser"
    private val password = "password123!"
    private lateinit var accessToken: String

    @BeforeEach
    fun setUp() {
        // 1. 테스트 사용자 생성
        userService.signUp(username, password)

        // 2. JWT 토큰 발급
        accessToken = jwtProvider.createAccessToken(username, "ROLE_USER")
    }

    @Test
    @DisplayName("로그인 후 내 정보 조회(/api/v1/users/me) 성공 테스트")
    fun `getCurrentUserInfo Success`() {
        // 3. API 호출 및 검증
        mockMvc.get("/api/v1/users/me") {
            header(HttpHeaders.AUTHORIZATION, "Bearer $accessToken")
        }.andExpect {
            status { isOk() }
            jsonPath("$.username") { value(username) }
            jsonPath("$.id") { exists() }
        }
    }

    @Test
    @DisplayName("인증 토큰 없이 내 정보 조회 시 401 에러 반환")
    fun `getCurrentUserInfo Unauthorized`() {
        mockMvc.get("/api/v1/users/me")
            .andExpect {
                status { isUnauthorized() }
            }
    }
}
