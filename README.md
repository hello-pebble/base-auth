# 🚀 High-Traffic Auth: The "Zero-Loading" Project

인증 서비스는 시스템의 관문입니다. 티켓팅이나 선착순 이벤트에서 발생하는 **'로그인 무한 로딩'**을 기술적으로 완전히 박멸하고, 10만 명 이상의 동시 접속을 견디는 초고성능 인증 엔진을 구축하는 프로젝트입니다.

---

## 🎯 The Vision: "로딩 0초 로그인"
우리는 단순히 '로그인 기능'을 구현하지 않습니다. 
**"트래픽 폭주 상황에서도 사용자가 '로그인' 버튼을 누르는 즉시 토큰을 발급받는 시스템"**을 지향합니다.

### 핵심 원칙
1.  **Stateless First**: 서버 대수를 늘리는 만큼 성능이 비례하여 증가하는 무상태 구조.
2.  **Safety & Stability**: Kotlin의 Null-Safety와 관용구를 활용한 극한 상황에서의 안정성.
3.  **Traffic Control**: 비정상은 즉시 차단(Cut)하고, 정상 유저는 스마트하게 대기(Wait)시키는 전략적 관문.

---

## 🗺️ Project Roadmap

| Phase | 핵심 가치 (Core Value) | 주요 성과 | 상태 |
| :--- | :--- | :--- | :---: |
| **[Phase 1](./docs/phase/phase1.md)** | **Foundation** | Java + 세션 기반 인증 및 Redis 연동 기초 설계 | ✅ |
| **[Phase 2](./docs/phase/phase2.md)** | **Stateless** | JWT 기반 Stateless 인증 전환 및 RTR(Rotation) 전략 수립 | ✅ |
| **[Phase 3](./docs/phase/phase3.md)** | **Expansion** | OAuth2(Google/GitHub) 연동 및 권한 체계 확립 | ✅ |
| **[Phase 3.5](./docs/phase/phase3_5.md)** | **Stability** | Kotlin 전면 전환 및 아키텍처 결합도 완화 | ✅ |
| **[Phase 4](./docs/phase/phase4.md)** | **The Gateway** | **[Traffic Control]** 비정상 요청 차단 및 가상 대기열 구축 | ⏳ |
| **Phase 5** | **The Engine** | 비동기(Coroutine/R2DBC) 전환으로 처리 한계 돌파 | 📅 |
| **Phase 7** | **Perfection** | GraalVM Native Image 및 100k CCU 스트레스 테스트 통과 | 📅 |

---

## 📑 Core Documentation
이 프로젝트의 설계 철학과 기술적 의사결정의 이유는 아래 문서에서 확인할 수 있습니다.

*   **[PROJECT_MANIFESTO.md](./docs/PROJECT_MANIFESTO.md)**: "왜 이 프로젝트를 하는가?" (컨셉 및 검증 시나리오)
*   **[DECISION_LOG_WHY.md](./docs/DECISION_LOG_WHY.md)**: 기술 선택의 이유와 트레이드오프 기록.
*   **[TRAFFIC_CONTROL_STRATEGY.md](./docs/TRAFFIC_CONTROL_STRATEGY.md)**: Phase 4의 핵심인 '차단과 대기' 전략.
*   **[ROADMAP_TO_ZERO_LOADING.md](./docs/ROADMAP_TO_ZERO_LOADING.md)**: 최종 목표까지의 기술적 이정표.

---

## 🛠️ Tech Stack
- **Language**: Kotlin 1.9 (Target JVM 21)
- **Framework**: Spring Boot 3.4
- **Security**: Spring Security (OAuth2, JWT)
- **Database**: PostgreSQL (Persistence), Redis (Token/Queue Management)
- **Testing**: JUnit 5, Mockito-Kotlin

---
**"기술은 수단일 뿐, 우리의 목표는 어떤 상황에서도 죽지 않는 인증 시스템입니다."**
