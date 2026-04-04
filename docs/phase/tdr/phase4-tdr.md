# Phase 4 TDR: Filtering & Smart Gateway

## 1. 개요 (Context)
로그인은 단순한 정보 조회가 아니라 보안 연산(Argon2 등)을 수반하므로, 공격자나 비정상 유저에 의한 '무차별 로그인 시도'는 시스템 전체의 성능을 고갈시키는 가장 주된 원인입니다.

## 2. 해결 방안 (Solution)

### 2-1. Filtering (Bucket4j)
`Bucket4j`를 사용한 **Token Bucket 알고리즘** 기반의 Rate Limiting을 도입합니다.

### 2-2. Queuing (Virtual Waiting Room)
서버 수용량 초과 시, **Redis Sorted Set**을 활용한 대기열을 도입합니다.

*   **왜 Redis Sorted Set인가?**
    *   `O(log N)`의 시간 복잡도로 수십만 명의 순위를 즉시 계산 가능.
    *   타임스탬프를 스코어(Score)로 활용하여 공정한 '선입선출(FIFO)' 보장.
*   **왜 Polling 방식인가?**
    *   수만 명의 웹소켓 연결 유지는 서버 메모리에 큰 부담을 줌.
    *   HTTP Polling은 서버를 Stateless하게 유지하면서도 대규모 트래픽 확장에 가장 유리함.

## 3. 트레이드오프 (Trade-offs)
*   **성능 vs 정합성**: 로컬 메모리(`ConcurrentHashMap`)를 사용하면 매우 빠르지만, 다중 서버 환경에서 총합 제한이 어긋날 수 있습니다. 이를 위해 Redisson 기반의 분산 버킷을 도입할 계획입니다.
*   **IP 오인 차단**: 공용 IP를 사용하는 환경에서 다수의 정상 유저가 차단될 가능성이 있으므로, 향후 고도화된 대기열(Waiting Room)을 통해 이를 보완할 예정입니다.

## 4. 검증 시나리오 (Success Criteria)
1.  초당 10회 이상의 과도한 요청 시 429 에러 응답 확인.
2.  차단된 요청이 DB 커넥션 풀을 소모하지 않는지 모니터링.
3.  정상 유저는 버킷 범위 내에서 지연 없이 서비스 이용 가능 확인.
