# 🏁 Roadmap to "Zero-Loading Login" (High-Traffic Focus)

본 문서는 티켓팅, 선착순 할인 이벤트 등 **폭발적 트래픽(Traffic Spike)** 상황에서도 사용자 로그인 단계에서 지연이 발생하지 않는 '로딩 0초' 인증 시스템을 구축하기 위한 단계별 로드맵입니다.

---

## 🛡️ Phase 4: 트래픽 제어 및 가상 대기열 (The Gateway) - [현재 진행 예정]
*   **핵심**: 정상 유저는 줄을 세워 보호하고(Wait), 비정상 요청은 즉시 쳐내는(Cut) 스마트 관문 구축.
*   **주요 과제**: 
    *   **Filtering (Bucket4j/IP-based)**: 비정상 트래픽(Bot/Abuse)을 감지하여 즉각 차단.
    *   **Virtual Waiting Room (Redis)**: 서버 수용량 초과 시, 요청을 Redis 대기열에 담고 순번(Batch)에 따라 처리 허용.
    *   **Traffic Shaping**: 급격한 트래픽 변동에도 서버 부하를 일정하게 유지(Smoothing).

## ⚙️ Phase 5: 비동기 논블로킹 전환 (The Engine) - [성능 도약 단계]
*   **핵심**: 'Thread per Request' 방식의 물리적 한계를 깨고, 적은 자원으로 수만 명을 동시 처리하는 엔진 교체.
*   **주요 과제**: 
    *   **Kotlin Coroutines 전면 도입**: I/O 대기 시간 동안 쓰레드를 점유하지 않고 반환하여, 서버 메모리 효율을 10배 이상 향상.
    *   **R2DBC (Reactive SQL) 전환**: 기존 JDBC의 블로킹 병목을 제거하여 DB 연동 과정에서의 '무한 로딩' 근본적 해결.
    *   **User Info Caching**: 로그인 시 매번 DB를 조회하지 않도록 핵심 유저 정보를 Redis에 캐싱하여 응답 속도(Latency) 극대화.

## 🌊 Phase 6: 고가용성 및 복구력 (The Resilience) - [인프라 확장 단계]
*   **핵심**: 서버 일부가 불능 상태가 되어도 서비스 전체는 중단되지 않는 탄력적 구조 확보.
*   **주요 과제**: 
    *   **Circuit Breaker (Resilience4j)**: DB나 외부 OAuth 서버 지연 시, 장애가 전체 시스템으로 전파되지 않도록 즉시 차단 및 폴백 처리.
    *   **HPA (Horizontal Pod Autoscaling)**: 트래픽 급증 시 1분 내에 서버 인스턴스를 수십 개로 자동 확장하는 쿠버네티스(K8s) 환경 최적화.
    *   **Chaos Engineering**: 의도적으로 서버를 죽여보는 테스트를 통해 시스템의 자가 치유 능력을 검증.

## 💎 Phase 7: "Zero-Loading" 완성 (The Perfection) - [최종 최적화]
*   **핵심**: 서버 구동 속도(Cold Start)와 자원 점유율을 극한까지 튜닝하여 '완벽한 성능' 달성.
*   **주요 과제**: 
    *   **GraalVM Native Image**: Java/Kotlin 앱을 네이티브 실행 파일로 빌드하여, 0.1초 만에 서버가 구동되게 함 (Scale-out 속도 극대화).
    *   **Stress Testing (100k+ CCU)**: 티켓팅 상황을 가정한 10만 명 동시 접속 테스트를 통과할 때까지 반복 튜닝.
    *   **Zero-Downtime Deployment**: 트래픽 폭주 중에도 서비스 중단 없이 패치를 적용할 수 있는 무중단 배포 체계 완성.

---

## 🚀 Vision: 우리는 어디로 가는가?
우리의 목표는 단순한 '로그인 기능'이 아닙니다. **"어떤 상황에서도 사용자가 '로그인' 버튼을 누르는 즉시 다음 화면을 볼 수 있는, 보이지 않는 강력한 입구"**를 만드는 것입니다.

---
**작성일**: 2026-04-04
**관리 책임**: Project Lead / Architect
