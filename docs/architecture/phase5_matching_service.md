# Matching & Chat Service Architecture

## 1. Overview
Spring Cloud MSA 기반의 매칭 및 1:1 대화 서비스 설계 문서입니다.

## 2. Core Logic: Top 3 Mutual Matching
- 사용자가 서로를 3위 이내(1, 2, 3위)로 지정했을 때만 매칭이 성립됩니다.
- 매칭 성공 시 즉시 전용 대화방 ID(UUID)가 생성됩니다.

## 3. Architecture Details
- **Multi-module**: `auth-module` (Port 8080), `matching-module` (Port 8081)
- **Database**: 
  - Auth: RDBMS (PostgreSQL/H2)
  - Matching: In-Memory (ConcurrentHashMap)
- **Security**: Shared Secret Key 기반의 JWT 검증 (Identity Provider & Resource Server 구조)

## 4. API Specification
- `GET /api/v1/matching/recommendations`: 랜덤 추천 리스트
- `POST /api/v1/matching/rank`: 상대방 순위 부여 (1~3)
- `PUT /api/v1/matching/exposure`: 프로필 노출 설정
- `GET /api/v1/matching/matches`: 내 매칭 목록 및 대화방 확인

## 5. Future Scalability
- Spring Cloud Gateway 및 Eureka 도입 예정
- OpenFeign을 이용한 서비스 간 통신
