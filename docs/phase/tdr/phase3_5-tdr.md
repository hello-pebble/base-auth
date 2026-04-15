# TDR-003.5: Kotlin 전환 안정화 및 순환 참조 해소 전략

## 1. 배경 (Context)
Java에서 Kotlin으로 전환한 직후, 기존 `SecurityConfig` 구조에서 기인한 빈 생성 순환 참조(`Circular Dependency`) 및 Kotlin의 엄격한 타입 체크로 인한 컴파일 오류가 발생함. 이를 해결하고 구조적 완성도를 높이기 위한 설계적 결정이 필요했음.

## 2. 해결 방안 및 결정 (Decision)

### 2.1. 인증 설정 클래스 분리 (`AuthConfig` 신설)
- **결정**: `PasswordEncoder` 빈 설정을 `SecurityConfig`에서 추출하여 별도의 `AuthConfig` 클래스로 정의함.
- **기술적 근거**:
  - `SecurityConfig`는 필터 체인 구성 시 다양한 비즈니스 서비스(`UserService`, `CustomOAuth2UserService` 등)를 의존함.
  - 동시에 해당 서비스들이 `PasswordEncoder`를 의존하게 되면서 `SecurityConfig`로의 역참조가 발생함.
  - 설정을 분리함으로써 의존성 그래프를 단순화하고 `BeanCurrentlyInCreationException`을 방지함.

### 2.2. Kotlin 관용구(Idioms)로의 리팩토링
- **결정**: Java의 `Optional`과 `Stream`을 Kotlin의 `Nullable type`과 `Collection function`으로 대체함.
- **기술적 근거**:
  - Kotlin 컴파일러는 Java의 `Optional` 처리 중 발생하는 `Type mismatch`를 엄격하게 관리함.
  - `firstOrNull()?.defaultMessage ?: ""`와 같은 표현식은 가독성을 높일 뿐만 아니라, 컴파일 시점에 Null-safety를 보장함.

## 3. 대안 검토 (Alternative Options)

- **옵션 1: `@Lazy` 사용**: 순환 참조를 끊기 위해 의존성 주입 시점에 `@Lazy`를 적용할 수 있으나, 이는 실제 구조를 개선하는 것이 아니라 문제의 발생 시점을 런타임으로 미루는 미봉책임.
- **옵션 2: 빈 설정 분리(채택)**: 책임에 따라 구성 요소를 나누는 클린 아키텍처 원칙에 부합하며, 테스트 및 유지보수 시에도 특정 설정만 독립적으로 다룰 수 있음.

## 4. 기대 효과 (Impacts)
- **런타임 안정성**: 애플리케이션 시작 시점에 발생할 수 있는 빈 생성 실패 가능성을 낮춤.
- **코드량 감소**: Boilerplate 코드(Lombok, Optional 등) 제거로 소스 코드의 유지보수 부담이 줄어듦.
- **테스트 격리**: 보안 설정 전체를 불러오지 않고도 암호화 모듈만 독립적으로 테스트 환경에 주입 가능함.