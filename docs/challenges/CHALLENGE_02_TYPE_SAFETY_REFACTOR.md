# 🏔️ Challenge 02: 자바의 장황함과 런타임 불안정성 극대화

## 1. 🚨 The Trigger (발단: 상황)
인증 시스템의 규모가 커지면서 로직이 갈수록 복잡해지고, 단 한 줄의 코드가 수만 명의 사용자 경험을 좌우하게 되었습니다. 개발팀 내부에서는 **"누군가 실수로 Null 하나를 놓치면 이벤트 날 대규모 장애가 터질 수 있다"**는 극심한 불안감과 함께, 보일러플레이트 코드에 가려진 핵심 성능 로직을 파악하는 데 드는 비용이 기하급수적으로 늘어나는 위기에 직면했습니다.

## 2. 💣 The Pain Point (문제 정의)
**"코드가 성능을 가리고, 타입이 우리를 지켜주지 못한다."**
자바의 장황한 문법은 로직의 가독성을 저해하여 유지보수 비용을 높였고, 무엇보다 Null-safety가 컴파일 타임에 보장되지 않아 극한 트래픽 상황에서 '언제 터질지 모르는 시한폭탄'을 안고 있는 것과 같았습니다.

## 3. 🛡️ The Constraints (제약 사항)
- **Zero Downtime Migration**: 서비스 운영 중에도 마이그레이션이 가능해야 함.
- **Seamless Interop**: 기존의 Spring Security 및 JPA 인프라와 100% 호환되어야 함.
- **Improved Performance**: 언어 전환이 런타임 성능 저하를 초래하지 않아야 함.

## 4. ⚔️ The Weapon (기술 선택: Kotlin & Functional Idioms)
우리는 Java에서 **Kotlin**으로의 전면적인 기술 전환을 결정했습니다.

| 선택 기술 | 해결하려는 핵심 이유 |
| :--- | :--- |
| **Null-Safety** | 컴파일 타임에 Null 가능성을 강제하여 런타임 안정성 극대화 |
| **Data Classes** | Boilerplate를 제거하고 도메인의 본질(Data)에만 집중 |
| **Functional Idioms** | 필터 체인 및 토큰 검증 로직을 선언적으로 작성하여 가독성 향상 |
| **Coroutines Ready** | 향후 비동기 처리를 위한 기반을 다지는 전략적 선택 |

## 5. 📉 The Proof (검증)
- **코드량**: 동일 로직 기준 약 **40% 이상의 코드 라인 감소** 달성.
- **안정성**: 마이그레이션 이후 `NullPointerException` 발생 빈도 **0건 기록**.
- **가독성**: 복잡한 JWT 필터 로직이 절반 이하의 코드로 압축되어, 핵심 보안 로직의 유지보수성 획기적 개선.

---
**이전 챌린지**: [Challenge 01: Stateless 전환](./CHALLENGE_01_CENTRALIZED_BOTTLENECK.md)
**관련 작업**: [Phase 3.5 (Kotlin Migration)](../phase/phase3_5.md)
