## 트러블슈팅

미인증 접근 시 HTML 리다이렉트 반환 문제
- **문제**:
  보호된 자원에 인증 없이 접근 시 Spring Security 기본 동작인 HTML 리다이렉트가 반환됩니다.
  REST API 환경에서는 클라이언트가 JSON 형태의 명확한 에러 응답을 기대하기 때문에 에러응답을 필요합니다.
- **원인**:
  Spring Security 는 기본적으로 미인증 요청을 로그인 페이지로 리다이렉트합니다. API 서버에서는 이 동작을 명시적으로 재정의해야 한다.
- **해결**:
  AuthenticationEntryPoint 를 커스텀 구현하여 401 응답과 JSON 메시지를 반환하도록 처리


```diff
$ curl -i http://localhost:8080/api/v1/users/me

HTTP/1.1 401 Unauthorized
{"message":"인증이 필요합니다."}
```