
## 📝 Technical Decision Record (TDR)

---
### 🧾 phase2

### 🔹 Phase 2-1: JWT 발급 전략

| 구분 | Access Token | Refresh Token |
|------|--------------|---------------|
| **용도** | 서비스 접근 권한 인증 | Access Token 재발급 |
| **만료 시간** | 15분 (Short-lived) | 7일 (Long-lived) |
| **전달 방식** | Authorization Header (Bearer) | HttpOnly Cookie |
| **포함 정보** | sub, roles, iat, exp | sub, iat, exp |

| 항목 | 내용 |
|------|------|
| **Decision** | Access Token(Header) + Refresh Token(Cookie) 이원화 전략 |
| **Rationale** | XSS 공격으로부터 Refresh Token을 보호하고, CSRF 위험이 낮은 헤더 방식을 Access Token에 적용하여 보안과 편의성 균형 확보 |
| **Security** | Refresh Token에 HttpOnly 옵션을 부여하여 자바스크립트를 통한 탈취 방지 |

---

### 🔹 Phase 2-2: Redis Refresh Token 관리

| 구분 | AS-IS (Phase 2-1) | TO-BE (Phase 2-2) | 효과 |
|------|-------------------|-------------------|------|
| **저장소** | 없음 (Stateless 지향) | **Redis (Refresh Token 전용)** | 토큰 제어권 확보 |
| **재발급** | 단순 유효성 검증 | **Redis 내 값 일치 여부 확인** | 비정상 토큰 차단 |
| **로그아웃** | 클라이언트 측 토큰 삭제 | **서버 측 Redis 데이터 삭제** | 즉각적인 세션 만료 |
| **보안** | 토큰 탈취 시 만료까지 유효 | **Token Rotation (1회용)** | 탈취된 토큰의 재사용 방지 |

| 항목 | 내용 |
|------|------|
| **Decision** | Redis 기반 Token Rotation (RTR) 전략 채택 |
| **Rationale** | Stateless의 한계(서버의 제어권 부재)를 보완하기 위해, Refresh Token만 상태를 관리하여 보안 사고 시 즉각적인 대응(로그아웃, 강제 세션 종료)이 가능하도록 설계 |
| **Trade-off** | Redis 인프라 의존성 발생 및 매 재발급 시 Redis I/O 비용 추가 |

#### 🛠️ 구현 근거 (Code Snippet)
```java
// UserController.java - 재발급 시 기존 토큰 무효화 및 신규 발급
@PostMapping("/api/v1/refresh")
public ResponseEntity<Void> refresh(HttpServletRequest request) {
    // Redis에 저장된 토큰과 클라이언트가 보낸 토큰 비교
    String savedToken = refreshTokenRepository.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("Refresh Token이 만료되었습니다."));

    if (!savedToken.equals(refreshToken)) {
        // 탈취된 토큰의 재사용 감지 시 즉시 전체 세션 무효화
        refreshTokenRepository.deleteByUsername(username);
        throw new RuntimeException("비정상적인 토큰 접근입니다.");
    }

    // 기존 토큰 삭제 후 신규 한 쌍 발급 (One-time use)
    refreshTokenRepository.deleteByUsername(username);
    String newAccessToken = jwtProvider.createAccessToken(username, role);
    String newRefreshToken = jwtProvider.createRefreshToken(username);
    refreshTokenRepository.save(username, newRefreshToken, refreshExpiration);
}
```

---

### 🔹 Phase 2-3: Stateless JWT 인증 완성

| 구분 | AS-IS (Phase 2-2) | TO-BE (Phase 2-3) | 효과 |
|------|-------------------|-------------------|------|
| **인증 방식** | Session + JWT (Hybrid) | **Pure JWT (Stateless)** | 서버 메모리 자원 절약 |
| **세션 유지** | `JSESSIONID` 쿠키 사용 | **쿠키 미사용 (No Session)** | CSRF 취약점 방어 강화 |
| **사용자 검증** | 매 요청 시 세션/DB 조회 가능 | **토큰 서명 검증 (Self-contained)** | DB/Redis 조회 부하 최소화 |
| **확장성** | 세션 클러스터링 필요 | **완벽한 수평 확장 (Scale-out)** | 무상태 서버 구성 가능 |

| 항목 | 내용 |
|------|------|
| **Decision** | `SessionCreationPolicy.STATELESS` 및 서명 기반 즉시 검증 |
| **Rationale** | 토큰 자체에 권한(`roles`)을 포함하여 매 요청마다 DB를 조회하지 않고도 인가 처리가 가능하도록 구현. 이를 통해 분산 환경에서의 성능 극대화 |
| **Trade-off** | 토큰이 탈취되었을 때 만료 전까지는 서버에서 개별적으로 무효화하기 어려움 (Refresh Token을 통한 RTR로 보완) |

#### 🛠️ 구현 근거 (Code Snippet)
```java
// SecurityConfig.java - 세션 생성 정책 변경
@Bean
public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
        .csrf(AbstractHttpConfigurer::disable) // REST API이므로 CSRF 비활성화
        .sessionManagement(session -> session
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS) // 세션 생성 방지
        )
        .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
    return http.build();
}
```

---