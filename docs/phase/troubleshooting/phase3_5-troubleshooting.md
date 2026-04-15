## Phase 3.5 트러블슈팅 (Kotlin 전환 안정화)

### 1. Kotlin Type Mismatch (String? vs String)
- **문제**: `GlobalExceptionHandler.kt`에서 Java 스타일의 `Stream`과 `Optional`을 사용하는 중, 반환 타입이 `String?`로 추론되어 `Map<String, String>`에 값을 넣을 수 없는 컴파일 오류 발생.
- **원인**: `it.defaultMessage`는 Nullable(`String?`)이며, 이를 `orElse`로 처리해도 최종 반환값이 여전히 Nullable로 추론되는 Kotlin의 엄격한 타입 시스템 특징 때문.
- **해결**: Kotlin의 표준 함수인 `firstOrNull()`과 Safe call(`?.`), Elvis 연산자(`?:`)를 조합하여 간결하게 해결함.

#### 🛠️ 해결 근거 (Code Snippet)
```kotlin
// 수정 전 (Java 스타일)
val message = e.bindingResult.fieldErrors.stream()
    .findFirst()
    .map { it.defaultMessage }
    .orElse("입력값이 올바르지 않습니다.") // 결과가 String?로 추론됨

// 수정 후 (Kotlin 관용구)
val message = e.bindingResult.fieldErrors
    .firstOrNull()?.defaultMessage ?: "입력값이 올바르지 않습니다." // 결과가 String으로 확정됨
```

### 2. SecurityConfig ↔ UserService 순환 참조
- **문제**: `SecurityConfig` -> `CustomOAuth2UserService` -> `UserService` -> `PasswordEncoder` -> `SecurityConfig` 형태의 순환 참조로 인해 애플리케이션 빌드 실패.
- **원인**: `PasswordEncoder` 빈이 보안 설정 파일인 `SecurityConfig` 내부에 정의되어 있어, 비즈니스 서비스 계층에서 암호화 기능을 사용하려 할 때 보안 설정 전체를 의존하게 됨.
- **해결**: `PasswordEncoder` 빈만 관리하는 독립된 설정 클래스(`AuthConfig.kt`)를 생성하여 의존성 순환 고리를 끊음.

### 3. Mockito @InjectMocks 및 Kotlin 생성자 충돌
- **문제**: `CustomOAuth2UserServiceTest` 실행 시 `NullPointerException` 발생.
- **원인**: Kotlin은 생성자 시점에 모든 non-nullable 필드에 값이 주입되어야 함. 하지만 기존 Java 테스트 코드에서는 `UserRepository`만 모킹하고 `UserService`를 모킹하지 않아, `InjectMocks` 시점에 Kotlin 클래스의 필수 의존성 주입이 실패함.
- **해결**: 테스트 코드의 모킹 대상을 `UserRepository`에서 실제 의존 클래스인 `UserService`로 변경하여 생성자 주입의 정합성을 맞춤.

#### 🛠️ 해결 근거 (Code Snippet)
```java
// CustomOAuth2UserServiceTest.java
@Mock
private UserService userService; // UserRepository 대신 실제 의존 대상인 UserService를 Mocking

@InjectMocks
private CustomOAuth2UserService oAuth2UserService; // 이제 Mockito가 생성자를 통해 정상 주입 가능
```

### 4. git status 시 Kotlin 소스 코드 추적 누락
- **문제**: Java 파일은 삭제되었으나 새로 추가된 Kotlin 파일들이 `Untracked` 상태로 남아 있어 빌드 환경에 따라 소스 인식이 안 되는 이슈.
- **원인**: Git 관리 범주 밖의 파일들을 명시적으로 `git add` 하지 않은 상태에서 빌드 도구가 작동함.
- **해결**: `src/main/kotlin/` 하위의 모든 변경 사항을 스테이징(`git add .`)하여 소스 코드 관리를 일원화함.