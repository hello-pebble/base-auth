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

    @PostMapping("/api/v1/login")
    fun login(@Valid @RequestBody request: LoginRequest): ResponseEntity<Any> {
        // 사용자 인증 처리 (SecurityContext 저장 생략 - Stateless 방식)
        val authentication = authenticationManager.authenticate(
            UsernamePasswordAuthenticationToken(request.username, request.password)
        )

        val role = authentication.authorities.firstOrNull()?.authority ?: "ROLE_USER"
        val accessToken = jwtProvider.createAccessToken(request.username, role)
        val refreshToken = jwtProvider.createRefreshToken(request.username)

        // Refresh Token 저장 (In-memory)
        refreshTokenRepository.save(request.username, refreshToken, refreshExpiration)

        // Refresh Token을 HttpOnly 쿠키로 설정
        val refreshTokenCookie = ResponseCookie.from("refreshToken", refreshToken)
            .httpOnly(true)
            .secure(false) // HTTPS 적용 시 true 권장
            .path("/")
            .maxAge(7 * 24 * 60 * 60)
            .sameSite("Strict")
            .build()

        val customUserDetails = authentication.principal as CustomUserDetails
        val response = UserResponse.from(customUserDetails.user)

        return ResponseEntity.ok()
            .header(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString())
            .header(HttpHeaders.AUTHORIZATION, "Bearer $accessToken")
            .body(response)
    }

    @PostMapping("/api/v1/refresh")
    fun refresh(request: HttpServletRequest, response: HttpServletResponse): ResponseEntity<Void> {
        val refreshToken = (request.cookies ?: emptyArray<Cookie>())
            .find { it.name == "refreshToken" }
            ?.value ?: throw RuntimeException("Refresh Token이 존재하지 않습니다.")

        if (!jwtProvider.validateToken(refreshToken)) {
            throw RuntimeException("유효하지 않은 Refresh Token입니다.")
        }

        val username = jwtProvider.getClaims(refreshToken).subject
        val savedToken = refreshTokenRepository.findByUsername(username)
            .orElseThrow { RuntimeException("저장된 Refresh Token이 없습니다.") }

        if (savedToken != refreshToken) {
            refreshTokenRepository.deleteByUsername(username)
            throw RuntimeException("Refresh Token이 일치하지 않습니다.")
        }

        refreshTokenRepository.deleteByUsername(username)

        val user = userService.findByUsername(username)
        val newAccessToken = jwtProvider.createAccessToken(username, user.role.name)
        val newRefreshToken = jwtProvider.createRefreshToken(username)

        refreshTokenRepository.save(username, newRefreshToken, refreshExpiration)

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

    @PostMapping("/api/v1/waiting-room/enter")
    fun enterService(
        authentication: Authentication,
        @RequestParam serviceId: String
    ): ResponseEntity<WaitingRoomService.WaitingStatus> {
        val status = waitingRoomService.register(authentication.name, serviceId)
        return ResponseEntity.ok(status)
    }

    @GetMapping("/api/v1/waiting-room/status")
    fun checkStatus(
        @RequestParam userId: String,
        @RequestParam serviceId: String
    ): ResponseEntity<WaitingRoomService.WaitingStatus> {
        val isAllowed = waitingRoomService.isUserAllowed(userId, serviceId)
        val status = if (isAllowed) "ALLOWED" else "WAITING"
        return ResponseEntity.ok(WaitingRoomService.WaitingStatus(status, 0))
    }

    @GetMapping("/api/v1/users/me")
    fun me(authentication: Authentication): ResponseEntity<UserResponse> {
        val response = UserResponse.from(userService.findByUsername(authentication.name))
        return ResponseEntity.ok(response)
    }

    @GetMapping("/api/v1/users/admin/check")
    @PreAuthorize("hasRole('ADMIN')")
    fun adminOnly(): ResponseEntity<String> = ResponseEntity.ok("관리자 인증 성공!")

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
            fun from(user: User): UserResponse = UserResponse(user.id, user.username, user.email)
        }
    }
}
