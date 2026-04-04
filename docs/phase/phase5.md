# Phase 5: 비동기 논블로킹 전환 (The Engine)

본 문서는 'Thread per Request' 모델의 물리적 한계를 극복하고, 동일한 자원으로 10배 이상의 트래픽을 처리하기 위한 비동기 아키텍처 전환 전략을 다룹니다.

---

## 1. ⚠️ 현재의 한계 (Current Limitations)

*   **쓰레드 고갈 (Thread Starvation)**: 현재 서블릿 기반 구조는 하나의 요청이 처리되는 동안(특히 DB I/O 대기 시) 쓰레드 하나를 완전히 점유함. 동시 접속자가 늘어나면 신규 요청을 받을 쓰레드가 없어 '무한 로딩' 발생.
*   **자원 낭비**: 요청을 처리하지 않고 대기 중인 쓰레드들이 메모리와 CPU 컨텍스트 스위칭 비용을 소모함.
*   **DB 커넥션 병목**: JDBC의 동기 방식은 HikariCP 커넥션 풀 크기에 강하게 종속되어, DB 응답이 지연될 경우 모든 쓰레드가 연쇄적으로 마비됨.

---

## 2. 🎯 해결 방향 (Proposed Solutions)

### A. Kotlin Coroutines 도입
*   **내용**: 블로킹이 발생하는 지점(I/O)에서 쓰레드를 점유하지 않고 '일시 중단(Suspend)'한 뒤, 작업이 완료되면 다시 재개하는 구조로 전환.
*   **효과**: 수백 개의 쓰레드로 수만 개의 동시 연결을 처리 가능.

### B. R2DBC (Reactive SQL) 전환
*   **내용**: 기존의 JDBC 기반 JPA를 걷어내고, 비동기 드라이버인 R2DBC로 전환하여 데이터베이스 연동 과정에서의 블로킹을 완전히 제거.
*   **효과**: DB 응답을 기다리는 동안 서버 자원을 다른 요청 처리로 돌릴 수 있음.

### C. Spring WebFlux or Coroutine Web 전환
*   **내용**: 서블릿 컨테이너(Tomcat) 대신 논블로킹 기반의 Netty 등을 활용하여 입구부터 출구까지 비동기 파이프라인 구축.

---

## 3. ⚖️ 고려해야 할 점 (Critical Considerations)

*   **전파 효과 (Propagation)**: 비동기 전환은 'All or Nothing'임. 필터, 인터셉터, 서비스, 리포지토리 중 하나라도 블로킹 로직이 남아있다면 전체 비동기 성능이 급격히 저하됨.
*   **MDC 및 Context 공유**: 쓰레드가 수시로 바뀌는 비동기 환경에서는 기존의 `ThreadLocal` 기반 보안 컨텍스트(`SecurityContextHolder`)나 로그 추적(MDC)이 동작하지 않음. 별도의 Context 전파 로직 필수.
*   **라이브러리 호환성**: 현재 사용 중인 모든 라이브러리(Spring Security, Redis 등)가 비동기(Reactive)를 완벽히 지원하는지 검증 필요.

---

## 🛠️ Phase 5 로드맵 (Execution Plan)

1.  **5-1. 기술 스택 교체**: `spring-boot-starter-web`을 제거하고 `spring-boot-starter-webflux` 및 `Coroutine` 의존성 추가.
2.  **5-2. Persistence 고도화**: `spring-data-jpa`를 `spring-data-r2dbc`로 전환하고 엔티티 설계 재검토.
3.  **5-3. 비동기 보안 체계**: `ReactiveSecurityContextHolder`를 활용한 비동기 인증 필터 재구현.
4.  **5-4. 성능 벤치마킹**: k6를 활용하여 Phase 4(동기) 대비 Phase 5(비동기)의 RPS 및 응답 지연 시간 비교 검증.

---

## 💡 최종 목표
**"DB가 지연되어도 서버의 요청 수용 능력(Throughput)은 꺾이지 않는, 진정한 고탄력(Resilient) 엔진 완성."**
