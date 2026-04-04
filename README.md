# 🚀 High-Traffic Auth: The "Zero-Loading" Project

인증 서비스는 시스템의 관문입니다. 티켓팅이나 선착순 이벤트에서 발생하는 **'로그인 무한 로딩'**을 기술적으로 완전히 박멸하고, 10만 명 이상의 동시 접속을 견디는 초고성능 인증 엔진을 구축하는 프로젝트입니다.

---

## 🎯 The Vision: "로딩 0초 로그인"
"트래픽 폭주 상황에서도 사용자가 '로그인' 버튼을 누르는 즉시 토큰을 발급받는 시스템" 을 목표로 진행합니다.

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
| **[Phase 4](./docs/phase/phase4.md)** | **The Gateway** | **[Traffic Control]** 비정상 IP 차단(Filtering) 및 가상 대기열(Queuing) 구축 | ✅ |
| **Phase 5** | **The Engine** | 비동기(Coroutine/R2DBC) 전환으로 쓰레드 점유 해소 및 처리 한계 돌파 | ⏳ |
| **Phase 7** | **Perfection** | GraalVM Native Image 및 100k CCU 스트레스 테스트 통과 | 📅 |

---

## ⚡ Current Milestone: Phase 4 (The Gateway)
우리는 트래픽 폭주 상황에서 서버의 생존을 보장하는 '스마트 관문'을 완성했습니다.

*   **Filtering (Cut)**: `Bucket4j`를 이용해 비정상 IP 요청을 즉시 쳐내어 서버 자원 보호.
*   **Queuing (Wait)**: 수용량 초과 시 Redis Sorted Set 대기열에 줄을 세워 시스템 마비 방지.
*   **Traffic Shaping**: 1초마다 고정된 배치(Batch) 인원만 진입시켜 서버 부하를 일정하게 유지.
*   **[시나리오 확인](./docs/phase/phase4.md#4-3-트래픽-제어-통합-시나리오-end-to-end-flow)**: 비정상 차단부터 정상 유저 대기까지의 전 과정 통합 완료.

---

## 🚀 Future Vision: Phase 5 (The Engine)
교통정리(Phase 4)가 끝났다면, 이제는 고속도로의 차선 자체를 늘릴 차례입니다.

*   **Non-blocking I/O**: `Kotlin Coroutines`를 도입하여 쓰레드 점유 없이 수만 명을 동시 처리.
*   **Reactive Data**: `R2DBC`로의 전환을 통해 DB 연동 과정의 모든 병목 제거.
*   **Extreme Performance**: 자주 조회되는 유저 정보를 메모리에 캐싱하여 응답 지연(Latency) 극대화 단축.

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

