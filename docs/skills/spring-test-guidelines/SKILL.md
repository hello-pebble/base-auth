---
name: spring-test-guidelines
description: Spring Boot 환경에서 JUnit 5, AssertJ, Mockito를 사용하여 고품질의 테스트 코드를 작성하기 위한 가이드라인입니다. 새로운 테스트를 작성하거나 기존 테스트를 리팩토링할 때 일관성, 가독성, 신뢰성을 보장하기 위해 사용합니다.
---

# Spring Boot 테스트 코드 작성 규칙

## 1. 기본 원칙
- **FIRST 원칙 준수**: Fast(빠르게), Independent(독립적으로), Repeatable(반복 가능하게), Self-Validating(자가 검증되게), Timely(적시에).
- **단일 책임**: 하나의 테스트 케이스는 하나의 시나리오만 검증합니다.
- **가독성 우선**: 테스트 코드는 그 자체로 문서의 역할을 해야 합니다.

## 2. 네이밍 컨벤션
- **메서드명**: `given_when_then` 패턴 또는 `상황_행위_결과` 형식을 권장합니다.
  - 예: `findUser_ExistingId_ReturnsUser()`, `join_DuplicateUsername_ThrowsException()`
- **테스트 클래스**: 대상 클래스명 뒤에 `Test` 또는 `Tests`를 붙입니다. (예: `UserServiceTest`)

## 3. 테스트 구조 (Gherkin 스타일)
모든 테스트는 다음 세 단계로 명확히 구분하여 작성합니다.
```java
@Test
void test_method() {
    // given (준비): 테스트를 위한 환경 설정 및 데이터 준비
    
    // when (실행): 검증하려는 로직 수행
    
    // then (검증): 예상한 결과가 나왔는지 확인 (AssertJ 사용)
}
```

## 4. 도구 및 라이브러리 활용
- **Assertion**: JUnit 5 기본 `Assertions` 대신 **AssertJ**의 `assertThat()`을 사용합니다.
  - 가독성이 높고 메서드 체이닝이 가능합니다.
- **Mocking**: 외부 의존성이나 복잡한 로직은 **Mockito**를 사용하여 격리합니다.
  - `@Mock`, `@InjectMocks`, `given()`, `verify()` 등을 활용합니다.

## 5. Spring Boot 슬라이스 테스트 전략
전체 컨텍스트를 로드하는 `@SpringBootTest` 대신 필요한 계층만 로드하는 슬라이스 테스트를 우선 고려합니다.
- **Controller**: `@WebMvcTest` + `MockMvc` 사용
- **Service**: `@ExtendWith(MockitoExtension.class)`를 사용한 순수 단위 테스트
- **Repository**: `@DataJpaTest` (H2와 같은 인메모리 DB 활용)

## 6. 체크리스트
- [ ] 테스트가 외부 환경(DB, API)에 의존하지 않고 독립적인가?
- [ ] Edge Case(null, 빈 값, 예외 상황)에 대한 검증이 포함되었는가?
- [ ] 불필요한 `print`문이 아닌 Assertion으로 결과를 검증하는가?
- [ ] 테스트 실행 후 데이터가 롤백되거나 정리되는가?
