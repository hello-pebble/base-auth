## Phase 2: JWT 인증 시스템 분석
단일 백엔드 내부 구조이므로 외부 토큰 전파/검증 항목 제외

#### 2-1. Access / Refresh 토큰 발급

| 항목              | 세부 내용                                               | 상태 |
|-------------------|---------------------------------------------------------|-----|
| 토큰 구조 설계    | payload claim 정의 (sub, roles, iat, exp)               | ✅ |
| 토큰 발급         | 로그인 성공 시 Access Token + Refresh Token 동시 발급   | ✅ |
| 토큰 전달 방식1   | Access Token → Authorization: Bearer 헤더 응답          | ✅ |
| 토큰 전달 방식2   | Refresh Token → HttpOnly Cookie 응답                    | ✅ |
| 만료 시간 설정    | Access Token 15분 / Refresh Token 7일                   | ✅ |


Phase 2-1: 토큰 발급 로직 구현
- **작업 내용**:
    - `jjwt` 0.12.6 버전 의존성 추가.
    - `JwtProvider` 구현: Access Token(15분), Refresh Token(7일) 발급 로직 작성.
    - `application.yaml`에 JWT 보안 키 및 만료 시간 설정 추가.
    - `UserController.login` 메서드 수정: 인증 성공 시 헤더(Access)와 쿠키(Refresh)로 토큰 전달.
- **특이 사항**:
    - 최신 jjwt API 반영 시 파서 빌더 구조 주의 필요.
    - 보안 강화를 위해 256비트 이상의 시크릿 키 적용.
---

#### 2-2. Redis Refresh Token 관리 

| 항목           | 세부 내용                                             | 상태 |
|----------------|-------------------------------------------------------|----|
| 저장 구조      | userId → refreshToken 형태로 Redis 저장 (TTL 동기화)  | ✅ |
| 재발급 검증    | 요청된 Refresh Token과 Redis 저장값 비교 후 재발급    | ✅ |
| Rotation 전략  | 재발급 시 기존 토큰 삭제 + 신규 토큰 저장 (1회용)     | ✅ |
| 로그아웃 처리  | 로그아웃 시 Redis에서 Refresh Token 즉시 삭제         | ✅ |

---

#### 2-3. 서명 기반 검증 필터

| 항목            | 세부 내용                                                 | 상태 |
|-----------------|--------------------------------------------------------------|----|
| 알고리즘 선택   | 단일 서버이므로 HMAC HS256 적용 (비대칭키 불필요)            | ✅ |
| 필터 구현       | ```JwtAuthenticationFilter``` — DB 조회 없이 서명만으로 검증 | ✅ |
| SecurityContext | 검증 성공 시 ```UsernamePasswordAuthenticationToken``` 주입  | ✅ |
| 세션 제거       | SessionCreationPolicy.STATELESS 적용, HttpSession 완전 제거  | ✅ |

---

#### 2-4. Kotlin 전환 전략 (Phase 2.5)
| 항목 | 세부 내용 | 상태 |
| :--- | :--- | :--- |
| 프로젝트 설정 | Kotlin 전용 빌드 스크립트 및 Standard Library 의존성 추가 | 🏗️ |
| 도메인 계층 전환 | `User`, `UserRole` 등 핵심 모델을 Kotlin(Data Class)으로 전환 | 🏗️ |
| 인프라 계층 전환 | `Security Config`, `JwtProvider` 등 핵심 인프라 코드 전환 | 🏗️ |
| 비즈니스 레이어 전환 | `Service`, `Controller` 등 비즈니스 로직의 점진적 Kotlin화 | 🏗️ |
| 안정성 검증 | Java/Kotlin 혼용 환경에서의 통합 테스트 통과 및 최적화 | 🏗️ |

**Phase 2-4 (Planned): Kotlin 전환 시나리오**
- **전환 시점**: Phase 2(JWT Stateless 인증 완성) 직후 진행.
- **전략**:
    - **안정성 최우선**: Java로 완성된 로직과 테스트 코드를 기준으로 "검증된 번역" 방식 채택.
    - **점진적 적용**: 도메인 → 인프라 → 비즈니스 로직 순으로 계층별 전환하여 리스크 최소화.
    - **최신성 확보**: Kotlin의 Null-safety와 간결한 문법을 적용하여 기술 부채 청산.

---

### 🔗 연관 자료
- **[TDR](tdr/phase2-tdr.md)**
- **[트러블슈팅](troubleshooting/phase2-troubleshooting.md)**