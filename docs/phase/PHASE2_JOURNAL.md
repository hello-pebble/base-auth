# Phase 2: JWT 기반 Stateless 인증 시스템 구현 저널

## 1. 계획 (Plan)
- [ ] Step 1: JWT 인프라 및 환경 설정
- [ ] Step 2: JWT 핵심 로직 (`JwtProvider`) 구현
- [ ] Step 3: Spring Security Stateless 전환
- [ ] Step 4: Login/Logout API 및 Redis 연동

## 2. TDR (Technical Design Record)

### [상태] 진행 중 (In Progress)
### [설계 방향]
- **인증**: Stateless JWT (Access Token 30분, Refresh Token 7일)
- **상태 관리**: **Redis** (Refresh Token 저장 및 로그아웃 블랙리스트 관리)
- **보안 전략**: Access Token은 Memory/Header, Refresh Token은 HttpOnly Cookie 또는 Secure Header 권장.

### [Rationale]
- 확장성을 위해 Access Token은 서버 저장소를 참조하지 않는 Stateless 방식으로 채택.
- 제어권을 유지하기 위해(로그아웃, 기기 차단 등) Refresh Token은 Redis를 통해 상태를 관리.

## 3. 트러블슈팅 및 로그
*(구현 과정에서 발생하는 이슈를 여기에 기록합니다)*
- `[YYYY-MM-DD]` : (예시) jjwt 버전 0.12.x 대에서 API 변경으로 인한 설정 오류 해결 예정...
