# Challenge 01: Centralized Authentication Bottleneck & SPOF

**상태**: 위험 식별 및 해결책 수립 완료
**연관 단계**: Phase 4 (Traffic Control Strategy)

---

## 🚨 위기 상황 (The Risk)

### 1. 전사 서비스의 마비 (Total Blackout)
통합 인증 서버는 모든 서비스(A, B, C...)의 관문입니다. 만약 인증 서버가 다운되면, 단일 서비스의 장애를 넘어 **사내 모든 시스템의 신규 진입이 불가능**해지는 '단일 장애점(SPOF, Single Point of Failure)' 문제가 발생합니다.

### 2. 인증 요청의 급증 (Traffic Surge)
- **이벤트성 폭주**: 대규모 인사 이동, 전사 공지 확인, 혹은 마케팅 이벤트 시 특정 시간에 인증 요청이 평소의 수십 배 이상 집중됨.
- **연쇄 장애 (Cascading Failure)**: 인증 서버의 응답이 느려지면, 연동된 서비스 A, B의 커넥션 풀이 가득 차게 되고, 결국 전체 생태계가 함께 무너지는 현상이 발생함.

---

## 🛠️ 전략적 해결책 (Strategic Solutions)

우리는 단순히 서버 사양을 높이는(Scale-up) 것이 아니라, **'시스템이 우아하게 버티는 방법'**을 설계했습니다.

### 1. 스마트 대기열 (Waiting Room Service)
- **개념**: 인증 서버의 가용 용량을 초과하는 요청이 들어올 경우, 즉시 '실패' 처리하지 않고 '가상 대기열'로 진입시킴.
- **효과**: 서버는 자신이 처리 가능한 적정 트래픽만 유지하며, 사용자에게는 예상 대기 시간을 안내하여 심리적 불안감을 해소함.

### 2. 지능형 차단 (Adaptive Rate Limiting)
- **개념**: 특정 IP나 특정 서비스에서 비정상적으로 많은 요청이 들어올 경우, 해당 경로의 트래픽만 선별적으로 제한함.
- **효과**: 특정 서비스의 장애나 공격이 다른 서비스의 인증 기회까지 박탈하는 것을 방어함.

### 3. 인증 가용성 보존 (Graceful Degradation)
- **개념**: 인증 서버의 메인 DB가 일시적으로 느려질 경우, 핵심 인증 정보(L1 Cache)를 기반으로 최소한의 로그인 상태 유지를 보장함.
- **효과**: 최악의 상황에서도 이미 로그인된 관리자의 세션은 보호하여 업무 연속성을 유지함.

---

## 📈 해결 후 기대 지표

| 지표 | 해결 전 | 해결 후 (목표) |
|:---|:---|:---|
| **내결함성 (Fault Tolerance)** | 인증 서버 장애 시 전사 마비 | 대기열 진입 및 순차적 처리로 서비스 유지 |
| **최대 동시 접속 수용량** | N (고정형) | 10N ~ 100N (대기열 기반 확장) |
| **장애 전파 (Cascading)** | 즉시 전파 | 대기열 및 서킷 브레이커를 통한 차단 |

---

## 🔗 상세 구현 설계
- **[Traffic Control Strategy](../TRAFFIC_CONTROL_STRATEGY.md)**: '차단과 대기'의 상세 알고리즘 및 아키텍처
- **[Phase 4 Architecture](../architecture/phase4_architecture.md)**: 스마트 게이트웨이 및 대기열 연동 흐름도
- **[Waiting Room Logic](../../src/main/kotlin/com/pebble/baseAuth/config/WaitingRoomService.kt)**: 실제 구현된 대기열 소스 코드
