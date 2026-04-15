# TDR: Phase 3 Service Integration & SSO Validation
---

## 1. 배경 (Context)
서비스 A, B, C가 독자적인 인증 체계를 가짐으로써 발생하는 관리자의 운영 피로도를 해소하고, 전사 차원의 통합 권한 제어 시스템을 구축해야 함. 이를 위해 중앙 인증 서버(Auth Server)를 중심으로 한 SSO 환경 조성이 필수적임.

---

## 2. 핵심 의사결정 (Technical Decisions)

### 2.1. 세션 공유 방식 결정: Redis vs Sticky Session
- **결정**: **Redis 기반 분산 세션 클러스터링 (Spring Session Redis)**
- **이유**:
  - Sticky Session은 로드밸런서에 의존적이며, 특정 서버 장애 시 해당 서버의 모든 세션이 유실됨.
  - Redis를 사용하면 서버의 무상태성(Stateless)을 유지하면서도, 서비스 A와 B가 실시간으로 세션을 공유하여 SSO를 구현하기에 가장 적합함.
- **트레이드오프**: Redis 인프라 관리 비용이 추가되지만, 운영 안정성과 SSO 구현의 용이성 측면에서 얻는 이득이 훨씬 큼.

### 2.2. 인증 프로토콜 선정: OAuth2/OIDC vs SAML
- **결정**: **OAuth2.0 / OpenID Connect (OIDC)**
- **이유**:
  - 최신 모바일/웹 환경에서 표준으로 자리 잡았으며, 커스텀 클레임(Claim)을 통한 권한 위임이 용이함.
  - SAML 대비 가벼운 JSON 기반 토큰을 사용하여 네트워크 부하를 줄일 수 있음.
  - 신규 서비스 및 오픈소스(Grafana, Jenkins 등)와의 연동 편의성이 압도적임.
- **트레이드오프**: SAML 대비 보안 설정이 복잡할 수 있으나, Spring Security 라이브러리의 강력한 지원으로 상쇄 가능.

### 2.3. 토큰 전달 방식: HttpOnly Cookie vs Local Storage
- **결정**: **HttpOnly / Secure Cookie**
- **이유**:
  - XSS(Cross-Site Scripting) 공격으로부터 토큰을 보호하기 위해 브라우저 스크립트가 접근할 수 없는 쿠키 방식을 채택.
  - CSRF(Cross-Site Request Forgery) 위험은 Spring Security의 CSRF Protection 및 `SameSite=Lax` 속성으로 방어 가능.
- **트레이드오프**: 프론트엔드에서 토큰을 직접 다루기 어려워지지만, 보안 안정성이 최우선 순위임.

---

## 3. 검증 결과 (Validation Results)

| 테스트 항목 | 결과 | 비고 |
|:---|:---|:---|
| **SSO 연동 성공률** | 100% | 서비스 A ↔ B 간 무중단 세션 이동 확인 |
| **권한 회수 지연 시간** | < 100ms | 중앙 서버 권한 변경 시 Redis를 통한 즉각 전파 확인 |
| **토큰 갱신 안정성** | Pass | Refresh Token을 통한 백그라운드 무중단 갱신 검증 |

---

## 4. 향후 과제
- **Phase 4**에서 대규모 트래픽 발생 시 SSO 서버 보호를 위한 **Rate Limiting** 및 **대기열(Waiting Room)** 시스템 도입 예정.
- 서비스 간 직접 통신 시의 mTLS(Mutual TLS) 도입 검토.

---

## 5. 승인 내역
- [x] 아키텍처 설계 적합성 검토 완료
- [x] 보안 가이드라인 준수 여부 확인
- [x] 운영 모니터링 체계 수립 완료