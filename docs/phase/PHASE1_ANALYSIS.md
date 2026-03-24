# Phase 1: 초기 세션 기반 인증 시스템 분석

## 1. TDR (Technical Design Record)

### [상태] 완료 (Done)
### [설계 요약]
- **인증 방식**: Spring Security + Servlet Session (JSESSIONID)
- **저장소**: 서버 인메모리 세션
- **비밀번호 암호화**: Argon2 (Argon2PasswordEncoder)
- **응답 형식**: JSON (CustomAuthenticationHandler)

### [결정 배경 (Rationale)]
- 초기 프로토타입 단계에서 구현의 단순성과 표준 보안 준수를 우선함.
- Argon2를 통해 높은 수준의 비밀번호 보안 확보.

### [한계점 (Bottlenecks)]
- **확장성**: 단일 서버 구조로, 서버 증설 시 세션 불일치 문제 발생 (Stateless화 필요).
- **성능**: 대규모 트래픽 시 세션 메모리 점유 및 DB 조회 부하 예상.

## 2. 트러블슈팅 및 발견 사항
- **Issue**: 초기 설정 시 CSRF 비활성화 필요 (API 서버 용도).
- **Solution**: `SecurityConfig`에서 `csrf().disable()` 적용.
- **Observation**: `Argon2` 연산이 강력하여 CPU 사용량이 높을 수 있으므로 부하 테스트 시 모니터링 필요.
