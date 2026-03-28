# Phase 2: JWT 기반 Stateless 인증 시스템 구현 저널

## 1. 진행 상태 (Progress)
- [x] Phase 2-1: Access / Refresh 토큰 발급 구현 (2026-03-26)
- [x] Phase 2-2: Redis Refresh Token 관리 (2026-03-26)

### Phase 2-2: Redis Refresh Token 관리 구현
- **작업 내용**:
    - `RefreshTokenRepository` 신설: Redis를 이용한 키-값 저장소 구현 (TTL 연동).
    - `UserController` 토큰 재발급(`/api/v1/refresh`) 엔드포인트 구현.
    - **Token Rotation 전략 적용**: 재발급 시 기존 Refresh Token 무효화 및 신규 한 쌍 발급.
    - `CustomAuthenticationHandler` 수정: 로그아웃 성공 시 Redis 토큰 즉시 삭제 로직 추가.
- **보안 강화**:
    - Redis에 저장된 토큰과 클라이언트가 보낸 토큰이 불일치할 경우, 탈취 시도로 간주하여 해당 사용자의 모든 세션을 종료(Redis 키 삭제)하도록 설계.

### Phase 2-3: Stateless JWT 인증 완성
- **작업 내용**:
    - `JwtAuthenticationFilter` 구현: HTTP 헤더에서 Access Token 추출 및 서명 검증.
    - `SecurityConfig` 전면 개편:
        - `SessionCreationPolicy.STATELESS` 설정 (세션 완전 제거).
        - `JwtAuthenticationFilter`를 보안 필터 체인에 추가.
        - CSRF, FormLogin 등 세션 기반 설정 비활성화.
    - `UserController` 리팩토링: 세션 관련 `SecurityContextRepository` 의존성 제거 및 로직 단순화.
- **성과**:
    - 서버 메모리를 차지하던 `HttpSession`을 완전히 제거하여 서버의 확장성 확보.
    - DB 조회 없이 토큰 서명만으로 인증이 가능하도록 개선하여 성능 향상.

## 2. 개발 기록 (Journal)

###  Phase 2-1: 토큰 발급 로직 구현
- **작업 내용**:
    - `jjwt` 0.12.6 버전 의존성 추가.
    - `JwtProvider` 구현: Access Token(15분), Refresh Token(7일) 발급 로직 작성.
    - `application.yaml`에 JWT 보안 키 및 만료 시간 설정 추가.
    - `UserController.login` 메서드 수정: 인증 성공 시 헤더(Access)와 쿠키(Refresh)로 토큰 전달.
- **특이 사항**:
    - 최신 jjwt API 반영 시 파서 빌더 구조 주의 필요.
    - 보안 강화를 위해 256비트 이상의 시크릿 키 적용.
