## Phase 2: JWT 인증 시스템 분석
단일 백엔드 내부 구조이므로 외부 토큰 전파/검증 항목 제외

#### 2-1. Access / Refresh 토큰 발급

| 항목              | 세부 내용                                               | 상태 |
|-------------------|---------------------------------------------------------|-----|
| 토큰 구조 설계    | payload claim 정의 (sub, roles, iat, exp)               | 🏗️ ️|
| 토큰 발급         | 로그인 성공 시 Access Token + Refresh Token 동시 발급   | 🏗️ ️|
| 토큰 전달 방식1   | Access Token → Authorization: Bearer 헤더 응답          | 🏗️ ️|
| 토큰 전달 방식2   | Refresh Token → HttpOnly Cookie 응답                    | 🏗️ ️|
| 만료 시간 설정    | Access Token 15분 / Refresh Token 7일                   | 🏗️ |

---

#### 2-2. Redis Refresh Token 관리 

| 항목           | 세부 내용                                             | 상태 |
|----------------|-------------------------------------------------------|----|
| 저장 구조      | userId → refreshToken 형태로 Redis 저장 (TTL 동기화)  | ️🏗️ ️️|
| 재발급 검증    | 요청된 Refresh Token과 Redis 저장값 비교 후 재발급    | ️🏗️ ️|
| Rotation 전략  | 재발급 시 기존 토큰 삭제 + 신규 토큰 저장 (1회용)     | ️🏗️ ️|
| 로그아웃 처리  | 로그아웃 시 Redis에서 Refresh Token 즉시 삭제         | ️🏗️ ️|

---

#### 2-3. 서명 기반 검증 필터

| 항목            | 세부 내용                                                 | 상태 |
|-----------------|--------------------------------------------------------------|----|
| 알고리즘 선택   | 단일 서버이므로 HMAC HS256 적용 (비대칭키 불필요)            |  ️🏗️  ️|
| 필터 구현       | ```JwtAuthenticationFilter``` — DB 조회 없이 서명만으로 검증 |  ️🏗️  ️|
| SecurityContext | 검증 성공 시 ```UsernamePasswordAuthenticationToken``` 주입  |  ️🏗️  ️|
| 세션 제거       | SessionCreationPolicy.STATELESS 적용, HttpSession 완전 제거  |  ️🏗️  ️|

---

### 🔗 연관 자료
- **[TDR](./docs/tdr/phase2-tdr.md)**
- **[트러블슈팅](./docs/troubleshooting/phase2-troubleshooting.md)**