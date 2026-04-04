## Phase 4: 트래픽 제어 및 대기열 관리 (Traffic Control & Waiting Room)

인증 시스템의 입구에서 트래픽을 정교하게 제어하여, 어떤 상황에서도 서버의 생존을 보장하고 사용자에게 예측 가능한 대기 경험을 제공합니다.

#### 4-1. 비정상 트래픽 차단 (Filtering)
| 항목 | 세부 내용 | 상태 |
| :--- | :--- | :---: |
| IP 기반 Rate Limit | Bucket4j를 활용하여 짧은 시간 내 과도한 요청을 보내는 IP 즉시 차단 (429 Too Many Requests) | 📅 |
| Brute-force 방어 | 로그인 실패 횟수에 따른 계정 임시 잠금 및 요청 제한 | 📅 |

#### 4-2. 가상 대기열 도입 (Queuing & Batching)
| 항목 | 세부 내용 | 상태 |
| :--- | :--- | :---: |
| Redis 기반 대기열 | 서버 수용량 초과 시, 요청을 즉시 처리하지 않고 Redis Sorted Set에 담아 순번 부여 | 📅 |
| 폴링/웹소켓 응답 | 사용자에게 "현재 대기 순번"을 반환하고, 서버가 처리 가능한 시점에 진입 허용 | 📅 |
| 처리량 조절 (Shaping) | 일정 단위(Batch)로 대기열의 유저를 통과시켜 서버 부하를 일정하게 유지 | 📅 |

#### 4-3. 트래픽 제어 통합 시나리오 (End-to-End Flow)

사용자가 로그인을 시도할 때 시스템이 트래픽을 제어하는 전체 흐름입니다.

1.  **입구 컷 (Filtering)**: 
    *   `RateLimitFilter`가 요청 IP를 확인. 
    *   1초 내 과도한 요청(매크로) 시 즉시 `429 Too Many Requests` 반환.
2.  **로그인 시도 (First Attempt)**: 
    *   사용자가 `POST /api/v1/login` 호출.
3.  **대기열 판단 (Queuing)**:
    *   `WaitingRoomService`가 서버 수용량(Capacity) 확인.
    *   **[즉시 허용]**: 수용량 여유 시 즉시 로그인 로직 수행 및 JWT 발급.
    *   **[대기 필요]**: 수용량 초과 시 `403 Forbidden`과 함께 **현재 대기 순번** 반환.
4.  **대기 및 폴링 (Waiting & Polling)**:
    *   클라이언트는 `GET /api/v1/waiting-room/status`를 주기적으로 호출하여 순번 확인.
    *   `WaitingRoomScheduler`가 1초마다 일정 인원(Batch)을 '진입 허용' 상태로 전환.
5.  **최종 진입 (Final Access)**:
    *   상태가 `ALLOWED`로 바뀌면 클라이언트는 다시 `POST /api/v1/login` 호출.
    *   서버는 '허용 명부' 확인 후 실제 인증 및 로그인 완료.

---

### 🔗 연관 자료
- **[트래픽 제어 전략 문서](../TRAFFIC_CONTROL_STRATEGY.md)**
