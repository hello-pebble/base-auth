# 📊 Java vs Kotlin 전환 및 아키텍처 종합 비교 보고서

본 문서는 프로젝트의 핵심 언어를 Java에서 Kotlin으로 전환한 결과와, 그 과정에서 개선된 아키텍처의 장단점 및 핵심 영역(Core)에 대한 분석을 담고 있습니다.

---

## 1. 아키텍처 및 언어 전환 비교 분석

| 구분 | 기존 (Java / Phase 1~3 초기) | 현재 (Kotlin / Phase 3.5) | 비고 |
| :--- | :--- | :--- | :--- |
| **언어 생산성** | Boilerplate(Getter/Setter, Constructor) 과다, Lombok 의존 | **Concise Code**: Data Class, Default Arguments 활용으로 코드량 대폭 감소 | Kotlin Idioms 적용 |
| **타입 안정성** | 런타임 NullPointerException(NPE) 위험 상존 | **Null-Safety**: 컴파일 시점 Null 체크 및 Safe Call(`?.`)로 안정성 확보 | `String?` vs `String` |
| **의존성 구조** | 단일 설정 파일 집중으로 인한 순환 참조 위험 | **Responsibility Segregation**: `AuthConfig` 분리로 순환 참조 근본 해결 | 구조적 유연성 확보 |
| **인증 체계** | ID/PW 중심, 세션 기반 흔적 잔존 | **Stateless JWT**: OAuth2 + JWT 통합 및 RTR 전략 완비 | 확장성 최적화 |
| **데이터 모델** | 가독성이 떨어지는 긴 엔티티 정의 | **Compact Model**: 간결한 생성자와 명확한 필드 정의 | 도메인 가독성 향상 |

---

## 2. Kotlin 전환의 주요 특징 및 평가

### ✅ 장점 (Benefits)
1. **코드 응집도 향상**: `User.kt`와 같은 도메인 모델이 간결해져 비즈니스 로직에 더 집중할 수 있습니다.
2. **방어적 프로그래밍**: Kotlin의 엄격한 타입 시스템이 `GlobalExceptionHandler` 사례와 같은 잠재적 버그를 사전 차단합니다.
3. **표현력 강화**: 표준 라이브러리의 풍부한 컬렉션 함수를 통해 복잡한 로직을 선언적으로 구현 가능합니다.

### ⚠️ 주의 및 제약 사항 (Constraints)
1. **프록시 메커니즘**: Kotlin 클래스는 기본이 `final`이므로, Spring의 AOP/프록시 기반 기능(`@Transactional` 등)을 위해 `plugin.spring` 설정이 필수적입니다.
2. **테스트 복잡도**: Mockito 사용 시 Kotlin의 non-nullable 제약 조건으로 인해 자바보다 정교한 모킹(Mocking) 전략이 요구됩니다.

---

## 3. 💡 핵심 인지 영역 (Core Focus)

유지보수 및 기능 확장 시 반드시 숙지해야 할 시스템의 핵심 엔진(Core)입니다.

### ① 보안 설정 체인 (Security Configuration Chain)
- **위치**: `AuthConfig` ↔ `SecurityConfig` ↔ `CustomOAuth2UserService` ↔ `UserService`
- **핵심**: 순환 참조 방지를 위해 `PasswordEncoder`가 별도 분리되어 있습니다. 보안 필터 추가 시 이 의존성 흐름을 깨지 않도록 주의해야 합니다.

### ② 토큰 생명주기 관리 (Token Lifecycle)
- **위치**: `JwtProvider`, `JwtAuthenticationFilter`, `RefreshTokenRepository` (Redis)
- **핵심**: Access Token(Cookie)과 Refresh Token(Redis)의 만료 시간 동기화 및 RTR(Rotation) 로직이 시스템의 보안 관문입니다.

### ③ 유저 데이터 통합 브릿지 (User Sync Bridge)
- **위치**: `CustomOAuth2UserService`, `User.kt`, `UserService`
- **핵심**: 소셜 로그인 유저는 비밀번호가 `null`인 상태로 관리됩니다. 소셜 정보와 로컬 계정의 통합/분리 정책이 `UserService`에 집약되어 있습니다.

---

## 4. 결론 및 향후 제언

현재의 Kotlin 전환은 단순히 언어를 바꾼 것을 넘어, **아키텍처의 결합도를 낮추고 타입 안정성을 확보**한 중요한 마일스톤입니다.

1. **도메인 확장성**: 향후 Kotlin Extension Function을 활용하여 비즈니스 로직을 더욱 깔끔하게 분리할 수 있는 기반이 마련되었습니다.
2. **비동기 준비**: 향후 고트래픽 대응 시 Kotlin Coroutines를 도입하기에 매우 유리한 구조를 갖추게 되었습니다.
3. **지속적 검증**: 현재 발생 중인 일부 통합 테스트 이슈를 해결하여 시스템의 전체적인 정합성을 완성하는 것이 다음 단계의 핵심입니다.
