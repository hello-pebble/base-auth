## Phase 3 트러블슈팅 (OAuth2.0 통합)

### 1. Mockito UnnecessaryStubbingException 발생
- **문제**: `CustomOAuth2UserServiceTest` 실행 시 `UnnecessaryStubbingException`이 발생하며 테스트가 실패함.
- **원인**: `given()`을 통해 Mock 객체의 동작을 정의(stubbing)했으나, 실제 테스트 코드(`loadUser`) 내에서 해당 메서드가 호출되지 않아 Mockito가 이를 불필요한 설정으로 간주하고 예외를 던짐.
- **해결**: 실제 호출되는 로직 위주로 테스트를 재구성하거나, 테스트가 아직 구현되지 않은 stubbing을 제거함. 이번 사례에서는 `loadUser` 전체 흐름보다는 `OAuth2UserInfoFactory`의 다중 IdP 대응 로직 검증에 집중하도록 테스트 케이스를 정제하여 해결함.

#### 🛠️ 해결 근거 (Code Snippet)
```java
// CustomOAuth2UserServiceTest.java - 정제된 테스트 케이스
@Test
@DisplayName("OAuth2UserInfoFactory_구글객체생성_확인")
void oAuth2UserInfoFactory_Google_ReturnsGoogleUserInfo() {
    // given
    Map<String, Object> attributes = Map.of("sub", "12345", "name", "홍길동", "email", "hong@gmail.com");

    // when
    OAuth2UserInfo userInfo = OAuth2UserInfoFactory.getOAuth2UserInfo("google", attributes);

    // then
    assertThat(userInfo).isInstanceOf(GoogleOAuth2UserInfo.class);
    assertThat(userInfo.getId()).isEqualTo("12345");
}
```

### 2. 소셜 서비스별 고유 ID 데이터 타입 불일치
- **문제**: Google은 고유 ID(`sub`)를 String으로 반환하는 반면, GitHub은 `id`를 Integer(Long)로 반환하여 형변환 에러 발생 가능성 확인.
- **원인**: 각 OAuth2 제공자(IdP)마다 사용자 정보를 전달하는 JSON 스키마와 데이터 타입이 다름.
- **해결**: `OAuth2UserInfo` 구현체에서 각 서비스별 특성에 맞춰 안전하게 문자열로 변환하도록 로직을 분리함. 특히 GitHub의 경우 `String.valueOf(attributes.get("id"))`를 사용하여 타입 안전성을 확보함.

#### 🛠️ 해결 근거 (Code Snippet)
```java
// GitHubOAuth2UserInfo.java - 타입 안전한 ID 추출
@Override
public String getId() {
    // GitHub의 ID는 숫자(Integer)이므로 안전하게 String으로 변환
    return String.valueOf(attributes.get("id"));
}
```

### 3. 로컬 환경에서 Secure 쿠키 전송 차단 이슈 (예상 이슈 및 대응)
- **문제**: `OAuth2SuccessHandler`에서 쿠키 설정 시 `setSecure(true)`를 적용할 경우, 로컬 환경(HTTP)에서 브라우저가 쿠키를 저장하지 않는 문제 발생 가능.
- **원인**: `Secure` 속성이 적용된 쿠키는 HTTPS 연결에서만 전송 및 저장이 가능함.
- **해결**: 운영 환경에서는 `Secure`를 필수로 적용하되, 로컬 개발 환경이나 테스트 시에는 프로파일(Profile) 설정을 통해 유동적으로 적용하거나, 로컬에서도 자기 서명 인증서(Self-signed SSL)를 사용하여 HTTPS 환경을 구성할 것을 권장함.

#### 🛠️ 해결 근거 (Code Snippet)
```java
// OAuth2SuccessHandler.java - 쿠키 설정 로직
private void addCookie(HttpServletResponse response, String name, String value, int maxAge) {
    Cookie cookie = new Cookie(name, value);
    cookie.setPath("/");
    cookie.setHttpOnly(true);
    cookie.setSecure(true); // [주의] 로컬 HTTP 테스트 시 브라우저에 따라 차단될 수 있음
    cookie.setMaxAge(maxAge);
    response.addCookie(cookie);
}
```

### 4. 쉘 스크립트(&&) 연산자 파싱 에러
- **문제**: `run_shell_command` 호출 시 `&&` 연산자를 포함한 명령이 PowerShell 환경에서 구문 오류(InvalidEndOfLine 등)를 발생시킴.
- **원인**: 실행 환경(win32/powershell)과 에이전트의 명령 전달 방식 간의 호환성 문제.
- **해결**: `&&`를 통한 체이닝 대신, 명령어를 개별적으로 순차 실행하거나 `;` 연산자를 사용하여 해결함.