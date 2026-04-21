package com.pebble.basicAuth.controller

import com.pebble.basicAuth.config.WaitingRoomService
import com.pebble.basicAuth.config.JwtProvider
import com.pebble.basicAuth.config.CustomUserDetails
import com.pebble.basicAuth.domain.User
import com.pebble.basicAuth.domain.UserService
import com.pebble.basicAuth.persistence.RefreshTokenRepository
import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseCookie
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*

@RestController
class UserController(
    private val userService: UserService,
    private val authenticationManager: AuthenticationManager,
    private val jwtProvider: JwtProvider,
    private val refreshTokenRepository: RefreshTokenRepository,
    private val waitingRoomService: WaitingRoomService
) {

    private val log = LoggerFactory.getLogger(this::class.java)

    @Value("\${jwt.refresh-expiration}")
    private var refreshExpiration: Long = 0

    @PostMapping("/api/v1/users/signup")
    fun signUp(@Valid @RequestBody request: UserSignUpRequest): ResponseEntity<UserResponse> {
        val response = UserResponse.from(userService.signUp(request.username, request.email, request.password))
        return ResponseEntity.status(HttpStatus.CREATED).body(response)
    }

    @GetMapping("/api/v1/users/admin/check")
    @PreAuthorize("hasRole('ADMIN')")
    fun adminOnly(): ResponseEntity<String> {
        return ResponseEntity.ok("관리자 인증 성공! 당신은 시스템 관리자입니다.")
    }

    @PostMapping("/api/v1/login")
    fun login(@Valid @RequestBody request: LoginRequest): ResponseEntity<Any> {
        // [Phase 4-2] 가상 대기열 체크: 허용된 유저인지 확인 (테스트를 위해 일시 주석 처리)
        /*
        if (!waitingRoomService.isUserAllowed(request.username)) {
            // 허용되지 않은 경우 대기열에 등록하고 순번 반환
            val status = waitingRoomService.register(request.username)
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(mapOf(
                "status" to "WAITING",
                "message" to "현재 접속 인원이 많아 대기 중입니다.",
                "rank" to status.rank
            ))
        }
        */

        // [Phase 2-1] 사용자 인증 처리
        val authentication = authenticationManager.authenticate(
            UsernamePasswordAuthenticationToken(request.username, request.password)
        )

        // [Phase 2-3] Stateless 전환으로 SecurityContextRepository.saveContext() 호출 불필요
        SecurityContextHolder.getContext().authentication = authentication

        // [Phase 2-1] JWT 토큰 발급
        val role = authentication.authorities.stream()
            .map { it.authority }
            .findFirst()
            .orElse("ROLE_USER")

        val accessToken = jwtProvider.createAccessToken(request.username, role)
        val refreshToken = jwtProvider.createRefreshToken(request.username)

        // [Phase 2-2] Refresh Token을 Redis에 저장 (Key: RT:{username})
        refreshTokenRepository.save(request.username, refreshToken, refreshExpiration)

        // [Phase 4-2] 로그인 성공 시 대기열 허용 명부에서 제거 (메모리 관리)
        waitingRoomService.removeFromProceed(request.username)

        // Refresh Token을 HttpOnly 쿠키로 설정
        val refreshTokenCookie = ResponseCookie.from("refreshToken", refreshToken)
            .httpOnly(true)
            .secure(false) // HTTPS 적용 전이므로 false (운영 시 true 권장)
            .path("/")
            .maxAge(7 * 24 * 60 * 60) // 7일
            .sameSite("Strict")
            .build()

        val customUserDetails = authentication.principal as CustomUserDetails
        val response = UserResponse.from(customUserDetails.user)

        // [Phase 2-1] Access Token을 Authorization 헤더에 담아 응답
        return ResponseEntity.ok()
            .header(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString())
            .header(HttpHeaders.AUTHORIZATION, "Bearer $accessToken")
            .body(response)
    }

    /**
     * [Phase 2-2] Refresh Token을 이용한 토큰 재발급 (Rotation 전략)
     */
    @PostMapping("/api/v1/refresh")
    fun refresh(request: HttpServletRequest, response: HttpServletResponse): ResponseEntity<Void> {
        // 쿠키에서 Refresh Token 추출
        val refreshToken = (request.cookies ?: emptyArray<Cookie>())
            .find { it.name == "refreshToken" }
            ?.value
            ?: throw RuntimeException("Refresh Token이 존재하지 않습니다.")

        // 토큰 유효성 및 서명 검증
        if (!jwtProvider.validateToken(refreshToken)) {
            throw RuntimeException("유효하지 않은 Refresh Token입니다.")
        }

        val claims = jwtProvider.getClaims(refreshToken)
        val username = claims.subject

        // Redis에 저장된 토큰과 비교 (보안 검증 및 중복 로그인 관리)
        val savedToken = refreshTokenRepository.findByUsername(username)
            .orElseThrow { RuntimeException("Redis에 저장된 Refresh Token이 없습니다. (이미 로그아웃되었거나 만료됨)") }

        if (savedToken != refreshToken) {
            // [Rotation 보안 전략] 재사용된 토큰 감지 시 즉시 삭제 (탈취 시도 차단)
            refreshTokenRepository.deleteByUsername(username)
            throw RuntimeException("Refresh Token이 일치하지 않습니다. 보안을 위해 모든 세션을 종료합니다.")
        }

        // [Phase 2-2] 기존 토큰 삭제 후 신규 토큰 한 쌍 발급 (1회용 전략)
        refreshTokenRepository.deleteByUsername(username)

        // 신규 토큰 생성
        val user = userService.findByUsername(username)
        val newAccessToken = jwtProvider.createAccessToken(username, user.role.name)
        val newRefreshToken = jwtProvider.createRefreshToken(username)

        // Redis에 신규 Refresh Token 저장
        refreshTokenRepository.save(username, newRefreshToken, refreshExpiration)

        // 신규 Refresh Token 쿠키 설정
        val newRefreshTokenCookie = ResponseCookie.from("refreshToken", newRefreshToken)
            .httpOnly(true)
            .secure(false)
            .path("/")
            .maxAge(7 * 24 * 60 * 60)
            .sameSite("Strict")
            .build()

        return ResponseEntity.ok()
            .header(HttpHeaders.SET_COOKIE, newRefreshTokenCookie.toString())
            .header(HttpHeaders.AUTHORIZATION, "Bearer $newAccessToken")
            .build()
    }

    @GetMapping("/api/v1/users/me")
    fun me(authentication: Authentication): ResponseEntity<UserResponse> {
        val response = UserResponse.from(userService.findByUsername(authentication.name))
        log.debug(response.toString())
        return ResponseEntity.ok(response)
    }

    data class UserSignUpRequest(
        @field:NotBlank(message = "사용자명은 필수입니다.")
        @field:Size(min = 4, max = 30, message = "사용자명은 4자 이상 30자 이하이어야 합니다.")
        val username: String,

        val email: String? = null,

        @field:NotBlank(message = "비밀번호는 필수입니다.")
        @field:Size(min = 8, message = "비밀번호는 최소 8자 이상이어야 합니다.")
        val password: String
    )

    data class LoginRequest(
        @field:NotBlank(message = "사용자명은 필수입니다.")
        val username: String = "",

        @field:NotBlank(message = "비밀번호는 필수입니다.")
        val password: String = ""
    )
    data class UserResponse(val id: Long?, val username: String, val email: String?) {
        companion object {
            fun from(user: User): UserResponse {
                return UserResponse(user.id, user.username, user.email)
            }
        }
    }
}
