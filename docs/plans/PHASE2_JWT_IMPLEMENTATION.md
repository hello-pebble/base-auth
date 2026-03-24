# Phase 2: JWT 기반 Stateless 인증 시스템 구현 계획

## 1. 개요
현재의 세션 기반 인증 시스템을 JWT(JSON Web Token) 방식으로 전환하여 확장성을 확보하고, Redis를 활용해 Refresh Token 및 로그아웃 블랙리스트를 관리함으로써 제어력을 유지합니다.

## 2. 주요 목표
- **Stateless 인증**: 서버 메모리에 세션을 저장하지 않고 토큰만으로 요청을 검증합니다.
- **Selective State (Redis)**: 보안이 중요한 Refresh Token은 Redis에서 관리하여 강제 로그아웃 등의 기능을 지원합니다.
- **보안 강화**: Access Token은 짧게, Refresh Token은 길게 설정하고 RTR(Refresh Token Rotation) 전략의 기반을 마련합니다.

## 3. 상세 단계별 태스크

### Step 1: 인프라 및 설정 준비
- [ ] **의존성 추가**: `jjwt` 라이브러리 추가
- [ ] **환경 설정**: `application.yaml`에 JWT 관련 프로퍼티 설정 (Secret Key, 만료 시간 등)
- [ ] **Redis 구성**: Refresh Token 저장을 위한 Redis 연동 확인

### Step 2: JWT 핵심 로직 구현
- [ ] **JwtProvider 클래스 생성**:
  - Access/Refresh Token 생성 로직
  - 토큰에서 사용자 정보(Claims) 추출
  - 토큰 유효성 및 만료 여부 검증
- [ ] **Token Response DTO**: 클라이언트에 반환할 토큰 구조 정의

### Step 3: Spring Security 고도화
- [ ] **SecurityConfig 수정**:
  - `SessionCreationPolicy.STATELESS` 설정
  - CSRF, Form Login, Http Basic 비활성화
- [ ] **JwtAuthenticationFilter 구현**:
  - 모든 요청을 가로채 Header에서 토큰 추출
  - 유효한 토큰일 경우 `SecurityContext`에 인증 정보 설정
- [ ] **인증 예외 처리**: `AuthenticationEntryPoint`를 통한 커스텀 401 응답 유지

### Step 4: 로그인 및 토큰 발급 로직 전환
- [ ] **Login API 수정**:
  - 인증 성공 시 세션 생성 대신 JWT 발급
  - Refresh Token을 Redis에 저장 (Key: 사용자 ID, Value: Refresh Token)
- [ ] **Logout API 수정**:
  - Redis에서 Refresh Token 삭제
  - Access Token 블랙리스트 처리 (선택 사항)

### Step 5: 토큰 갱신(Reissue) 로직 구현
- [ ] **Reissue API**: Refresh Token을 검증하고 새로운 Access Token 발급

## 4. 테스트 및 검증 계획
- [ ] **단위 테스트**: `JwtProvider`의 토큰 생성 및 파싱 검증
- [ ] **통합 테스트**: 
  - 로그인 후 발급된 토큰으로 인증이 필요한 API 호출 성공 확인
  - 만료된 토큰 사용 시 401 오류 반환 확인
  - Redis에 Refresh Token이 정상적으로 저장/삭제되는지 확인

## 5. 예상 결과물
- `com.pebble.baseAuth.config.JwtProvider`
- `com.pebble.baseAuth.config.JwtAuthenticationFilter`
- `com.pebble.baseAuth.controller.dto.TokenResponse`
- 수정된 `SecurityConfig` 및 `UserController`
