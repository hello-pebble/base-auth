# TDR-003: OAuth2.0 인증 성공 시 토큰 전달 전략

## 1. 개요 (Context)
OAuth2.0 기반 소셜 로그인이 성공한 후, 백엔드에서 생성한 JWT(Access & Refresh Token)를 어떻게 안전하게 클라이언트로 전달할지에 대한 결정이 필요함.

## 2. 제안된 옵션들 (Options)

### 옵션 1: Query Parameter (URL 파라미터)
- **전달 방식**: `https://frontend.url/callback?token=...`
- **장점**: 구현이 매우 단순하고, 프론트엔드에서 저장 위치(LocalStorage 등)를 자유롭게 선택 가능.
- **단점**: URL에 토큰이 노출되어 히스토리나 로그에 남을 수 있으며, 보안상 취약함.

### 옵션 2: HttpOnly Cookie (쿠키 방식) - **채택됨**
- **전달 방식**: `Set-Cookie: accessToken=...; HttpOnly; Secure; SameSite=Strict`
- **장점**: JavaScript로 접근이 불가능하여 XSS(Cross-Site Scripting) 공격에 대해 높은 보안성을 제공함.
- **단점**: CSRF(Cross-Site Request Forgery) 공격에 노출될 수 있으나, SameSite 설정 및 추가 방어 기법으로 보완 가능함.

## 3. 결정 사항 (Decision)
**옵션 2(HttpOnly Cookie)**를 채택함.

### 선정 이유
1. **보안성 최우선**: 사용자의 인증 정보인 JWT는 JavaScript 코드에 의해 탈취되지 않아야 함.
2. **Stateless 원칙 유지**: 쿠키를 사용하되 세션은 생성하지 않는 `STATELESS` 정책을 고수함으로써 서버 확장성을 유지함.
3. **일관성**: 기존 로그인 방식에서도 Refresh Token을 쿠키로 관리하고 있었으므로, 일관된 토큰 관리 전략을 가져갈 수 있음.

## 4. 구현 상세 (Implementation Details)
- **Access Token**: 유효기간 15분, HttpOnly 쿠키로 전달.
- **Refresh Token**: 유효기간 7일, HttpOnly 쿠키로 전달 및 Redis 저장.
- **쿠키 속성**:
  - `HttpOnly`: JS 접근 차단 (XSS 방어).
  - `Secure`: HTTPS를 통해서만 전송.
  - `Path=/`: 모든 도메인 경로에서 유효.
  - `SameSite=Lax`: (추가 예정) CSRF 방어를 위한 기본 브라우저 정책 활용.

## 5. CSRF 방어 전략 및 기술적 근거
현재 REST API 서버로서 CSRF를 비활성화(`csrf().disable()`)했으나, 쿠키 방식을 채택함에 따라 다음과 같은 추가 방어 계층을 고려함:
1. **SameSite 속성 활용**: 브라우저 레벨에서 타 도메인의 쿠키 전송을 제한하는 `SameSite=Lax` 또는 `Strict`를 적용하여 대부분의 CSRF 공격을 원천 차단 가능함.
2. **Custom Header 검증**: 클라이언트에서 `X-Requested-With`와 같은 커스텀 헤더를 요구함으로써, 표준 HTML Form 전송을 통한 CSRF 공격을 방어할 수 있음.
3. **Stateless 한계 인정**: 현재는 MVP 단계이므로 `SameSite` 정책에 의존하며, 향후 민감한 자원 변경 API(POST/PUT/DELETE)에는 별도의 CSRF 토큰 발급을 검토함.

## 6. 영향도 (Impacts)
- 프론트엔드에서는 요청 시 별도의 토큰 주입 코드가 필요 없으나, `withCredentials: true` 설정이 필요함.
- CORS 설정 시 `allowCredentials(true)`와 구체적인 `allowedOrigins` 지정이 필수적임.
