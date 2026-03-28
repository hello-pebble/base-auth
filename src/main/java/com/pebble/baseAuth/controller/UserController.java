package com.pebble.baseAuth.controller;

import com.pebble.baseAuth.config.JwtProvider;
import com.pebble.baseAuth.domain.User;
import com.pebble.baseAuth.domain.UserRole;
import com.pebble.baseAuth.domain.UserService;
import com.pebble.baseAuth.persistence.RefreshTokenRepository;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtProvider jwtProvider;
    private final RefreshTokenRepository refreshTokenRepository;

    @Value("${jwt.refresh-expiration}")
    private long refreshExpiration;

    @PostMapping("/api/v1/users/signup")
    public ResponseEntity<UserResponse> signUp(@RequestBody UserSignUpRequest request) {
        UserResponse response = UserResponse.from(userService.signUp(request.username(), request.password()));
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/api/v1/users/admin/check")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> adminOnly() {
        return ResponseEntity.ok("관리자 인증 성공! 당신은 시스템 관리자입니다.");
    }

    @PostMapping("/api/v1/login")
    public ResponseEntity<UserResponse> login(@ModelAttribute LoginRequest request) {
        // [Phase 2-1] 사용자 인증 처리
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password())
        );

        // [Phase 2-3] Stateless 전환으로 SecurityContextRepository.saveContext() 호출 불필요
        // SecurityContextHolder 설정은 필터에서 수행되거나 한 번의 요청 범위 내에서만 유효함
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // [Phase 2-1] JWT 토큰 발급
        String role = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .findFirst()
                .orElse("ROLE_USER");

        String accessToken = jwtProvider.createAccessToken(request.username(), role);
        String refreshToken = jwtProvider.createRefreshToken(request.username());

        // [Phase 2-2] Refresh Token을 Redis에 저장 (Key: RT:{username})
        refreshTokenRepository.save(request.username(), refreshToken, refreshExpiration);

        // Refresh Token을 HttpOnly 쿠키로 설정
        ResponseCookie refreshTokenCookie = ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true)
                .secure(false) // HTTPS 적용 전이므로 false (운영 시 true 권장)
                .path("/")
                .maxAge(7 * 24 * 60 * 60) // 7일
                .sameSite("Strict")
                .build();

        UserResponse response = UserResponse.from(userService.findByUsername(request.username()));
        
        // [Phase 2-1] Access Token을 Authorization 헤더에 담아 응답
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .body(response);
    }

    /**
     * [Phase 2-2] Refresh Token을 이용한 토큰 재발급 (Rotation 전략)
     */
    @PostMapping("/api/v1/refresh")
    public ResponseEntity<Void> refresh(HttpServletRequest request, HttpServletResponse response) {
        // 쿠키에서 Refresh Token 추출
        String refreshToken = Arrays.stream(request.getCookies() != null ? request.getCookies() : new Cookie[0])
                .filter(cookie -> cookie.getName().equals("refreshToken"))
                .map(Cookie::getValue)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Refresh Token이 존재하지 않습니다."));

        // 토큰 유효성 및 서명 검증
        if (!jwtProvider.validateToken(refreshToken)) {
            throw new RuntimeException("유효하지 않은 Refresh Token입니다.");
        }

        Claims claims = jwtProvider.getClaims(refreshToken);
        String username = claims.getSubject();

        // Redis에 저장된 토큰과 비교 (보안 검증 및 중복 로그인 관리)
        String savedToken = refreshTokenRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Redis에 저장된 Refresh Token이 없습니다. (이미 로그아웃되었거나 만료됨)"));

        if (!savedToken.equals(refreshToken)) {
            // [Rotation 보안 전략] 재사용된 토큰 감지 시 즉시 삭제 (탈취 시도 차단)
            refreshTokenRepository.deleteByUsername(username);
            throw new RuntimeException("Refresh Token이 일치하지 않습니다. 보안을 위해 모든 세션을 종료합니다.");
        }

        // [Phase 2-2] 기존 토큰 삭제 후 신규 토큰 한 쌍 발급 (1회용 전략)
        refreshTokenRepository.deleteByUsername(username);

        // 신규 토큰 생성
        User user = userService.findByUsername(username);
        String newAccessToken = jwtProvider.createAccessToken(username, user.getRole().name());
        String newRefreshToken = jwtProvider.createRefreshToken(username);

        // Redis에 신규 Refresh Token 저장
        refreshTokenRepository.save(username, newRefreshToken, refreshExpiration);

        // 신규 Refresh Token 쿠키 설정
        ResponseCookie newRefreshTokenCookie = ResponseCookie.from("refreshToken", newRefreshToken)
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(7 * 24 * 60 * 60)
                .sameSite("Strict")
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, newRefreshTokenCookie.toString())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + newAccessToken)
                .build();
    }

    @GetMapping("/api/v1/users/me")
    public ResponseEntity<UserResponse> me(Authentication authentication) {
        UserResponse response = UserResponse.from(userService.findByUsername(authentication.getName()));
        return ResponseEntity.ok(response);
    }

    public record UserSignUpRequest(String username, String password) {}
    public record LoginRequest(String username, String password) {}
    public record UserResponse(Long id, String username) {
        public static UserResponse from(User user) {
            return new UserResponse(user.getId(), user.getUsername());
        }
    }
}
