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
**"서버가 죽지 않는 인증 관문(Smart Gateway) 구축"**  
단순한 로그인을 넘어, 트래픽 폭주 시나리오에서 시스템을 보호하는 '교통 제어 시스템'을 완성했습니다.

### 🚥 시나리오별 대응 전략 (Traffic Control Logic)

| 상황 (Scenario) | 우리의 해결책 (Solution) | 기대 효과 및 설계 근거 (Why?) |
| :--- | :--- | :--- |
| **비정상 매크로 공격** | **[IP 기반 즉시 차단](./docs/phase/tdr/phase4-tdr.md#2-1-filtering-bucket4j)** | 1초에 수십 번 요청하는 비정상 IP를 필터 계층에서 즉시 쳐내어 DB 부하를 원천 봉쇄함. |
| **티켓팅 등 접속 폭주** | **[가상 대기열(Queuing)](./docs/TRAFFIC_CONTROL_STRATEGY.md#2-2-queuing-virtual-waiting-room)** | 무작정 접속을 허용하지 않고 Redis에 줄을 세워, 서버가 감당 가능한 만큼만 순차적으로 진입시킴. |
| **급격한 트래픽 스파이크** | **[Traffic Shaping](./docs/architecture/phase4_architecture.md#4-데이터-흐름)** | 1초마다 고정된 인원(Batch)만 통과시켜, CPU 점유율이 튀는 현상을 방지하고 처리량을 일정하게 유지(Smoothing)함. |

---

## ⚠️ Phase 4의 한계와 다음 단계 (The Bridge to Phase 5)
Phase 4에서 '교통 정리'는 성공했지만, 근본적으로 **차선(쓰레드)의 개수** 자체가 부족한 문제는 여전히 남아있습니다.

*   **현재의 한계 (Phase 4)**: 
    *   **Blocking I/O**: 대기열에 있는 유저를 처리하는 동안 서버의 쓰레드가 계속 점유되어, 실제 처리량(Throughput) 증대에는 한계가 있음.
    *   **Ghost User**: 대기열에 들어왔다가 이탈한 유저를 완벽하게 정제하지 못하는 자원 낭비 발생.
*   **해결책 (Phase 5)**: 
    *   **Coroutine**: 쓰레드 점유 없이 수만 명을 동시에 다루는 비동기 엔진으로 전환.
    *   **자원 효율화**: 실시간 Heartbeat 도입으로 이탈한 유저를 즉시 제거하여 대기열 회전율 극대화.

---

## 📑 Core Documentation (Deep Dive)
설계 철학과 기술적 의사결정의 상세 내역은 아래 문서들을 참고하세요.

*   **[PROJECT_MANIFESTO.md](./docs/PROJECT_MANIFESTO.md)**: 전체 프로젝트의 존재 이유와 검증 시나리오.
*   **[TRAFFIC_CONTROL_STRATEGY.md](./docs/TRAFFIC_CONTROL_STRATEGY.md)**: Phase 4의 핵심 전략인 '차단과 대기'의 상세 설계.
*   **[DECISION_LOG_WHY.md](./docs/DECISION_LOG_WHY.md)**: 기술 선택 시의 고민과 트레이드오프 기록.
*   **[PHASE 4 Architecture](./docs/architecture/phase4_architecture.md)**: 스마트 게이트웨이의 데이터 흐름도.

---

## 🛠️ Tech Stack
- **Language**: Kotlin 1.9 (Target JVM 21)
- **Framework**: Spring Boot 3.4
- **Security**: Spring Security (OAuth2, JWT)
- **Database**: PostgreSQL (Persistence), Redis (Token/Queue Management)
- **Testing**: JUnit 5, Mockito-Kotlin

