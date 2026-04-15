# Phase 4 Troubleshooting: Filtering Issues

## 1. 프록시 환경에서의 클라이언트 IP 누락
*   **증상**: 로드밸런서(AWS ALB, Nginx) 뒤에서 서버가 구동될 때, 모든 요청이 로드밸런서의 IP로 기록되어 모든 사용자가 하나의 버킷을 공유하는 문제.
*   **해결**: `X-Forwarded-For` 헤더를 최우선으로 확인하도록 `RateLimitFilter` 로직 보완.

## 2. Redisson 호환성 문제 (Lettuce와의 충돌)
*   **증상**: 스프링 부트 기본 `lettuce` 라이브러리와 `redisson`이 동시에 설정될 때 발생하는 Bean 충돌.
*   **해결**: `RedissonAutoConfiguration`을 커스텀하거나, 설정 파일에서 Redis 연결 정보 명시적 분리.

## 3. 429 응답 시의 클라이언트 사이드 대응
*   **증상**: 프론트엔드에서 429 에러를 받았을 때 단순히 실패로 처리하여 사용자가 혼란을 겪는 상황.
*   **해결**: 응답 JSON에 `message`를 명확히 포함하고, 프론트엔드에서 "잠시 후 시도" 가이드를 노출하도록 협의.

## 5. 대기열 이탈 유저의 좀비 데이터 (Abandoned Users)
*   **증상**: 대기열에 들어온 후 브라우저를 닫은 유저가 Redis Sorted Set에 계속 남아 메모리를 소모하는 문제.
*   **해결**: `ZREMRANGEBYSCORE`를 사용하여 일정 시간(예: 10분) 이상 응답이 없는 유저를 주기적으로 정리하는 배치 로직 추가 예정.

## 6. 대기열 우회 공격 (Waiting Room Bypass)
*   **증상**: 대기열 API를 거치지 않고 직접 로그인 API를 호출하여 서버 부하를 일으키는 경우.
*   **해결**: 실제 로그인 처리 로직(`UserService`) 입구에서 `WaitingRoomService.isUserAllowed()`를 호출하여, 허가된 명부(Set)에 없는 유저는 즉시 거부하도록 설계.