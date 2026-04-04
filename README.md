### 로그인 서비스

인증은 모든 서비스에서 공통적으로 사용되면서도 트래픽과 보안이 동시에 중요한 영역이라고 생각해서 
인증 서비스를 직접 설계하는 프로젝트를 진행했습니다.

---

## 🚀 Project Roadmap

인증/인가 서비스의 단계별 설계 및 구현 기록입니다.

| Phase                                                                                                           | 구현                                | TDR                                                                       | 트러블슈팅                                                                  | 상태  |
|:----------------------------------------------------------------------------------------------------------------|:----------------------------------|:--------------------------------------------------------------------------|------------------------------------------------------------------------|:---:|
| **[Phase 1](./docs/phase/phase1.md)**<br/> 초기 구축 및 기본 보안<br/>[아키텍처](./docs/architecture/phase1_architecture.md) | Security 필터, UserDetailsService   | [Redis 기반 세션, Argon2 해시](docs/phase/tdr/phase1-tdr.md)                    | [미인증 접근](docs/phase/troubleshooting/phase1-troubleshooting.md)         |  ✅  |
| **[Phase 2](./docs/phase/phase2.md)**<br/>JWT 기반 인증 전환<br/> [아키텍처](./docs/architecture/phase2_architecture.md)  | 토큰 발급, Redis 관리,JWT 필터, Kotlin 전환 | [토큰 발급, Redis Refresh, 검증 필터](docs/phase/tdr/phase2-tdr.md) · Java→Kotlin | [jjwt 0.12.6 변경](docs/phase/troubleshooting/phase2-troubleshooting.md) | ✅ |
| **[Phase 3](./docs/phase/phase3.md)**<br/>기능 확장<br/> [아키텍처](./docs/architecture/phase3_architecture.md)         | OAuth2 연동, Role 접근 제어                    | [OAuth2, RBAC](docs/phase/tdr/phase3-tdr.md)                              | [OAuth2](docs/phase/troubleshooting/phase3-troubleshooting.md)         | ✅  |
| **[Phase 3.5](./docs/phase/phase3_5.md)**<br/>Kotlin 안정화 및 구조 고도화 | Kotlin 관용구 적용, 순환 참조 해결, 테스트 안정화 | Kotlin 상호운용성, 순환 참조 해결 전략 | Kotlin 전환 이슈 해결 | ✅ |
| **Phase 4**<br/>안정성 및 트래픽 대응                                                                                    | Bucket4j, 분산 락             | Rate Limiting, Race Condition                                             | 트러블슈팅                                                                  | 📅  |
| **Phase 5**<br/>Reliability & Visibility                                                                        | MDC 로깅, Grafana, Zipkin           | 로그 수집, 모니터링, 트레이싱                                                         | 트러블슈팅                                                                  | 📅  |
| **부록** <br/> 사용자 관리                                                                                             | 회원가입, 비밀번호 변경, 탈퇴                 | 설계                                                                        | —                                                                      | 📅  |
=======

---
