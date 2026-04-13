# Phase 3 Architecture: Service Integration & SSO Validation

Phase 3의 아키텍처는 파편화된 각 서비스(Service A, B, C)를 중앙 인증 서버(Auth Server)와 연결하여 **단일 진실의 원천(SSO)**을 실현하는 구조를 가집니다.

---

### 🗺️ 전사 통합 인증 아키텍처

```mermaid
graph TD
    subgraph "Legacy (Before)"
        S1[Service A] --- DB1[(DB A)]
        S2[Service B] --- DB2[(DB B)]
        Admin1((Admin)) -.->|Repeat Login| S1
        Admin1 -.->|Repeat Login| S2
    end

    subgraph "Phase 3 (Unified Auth Ecosystem)"
        direction TB
        Auth[<b>Auth Server</b><br/>SSO / OAuth2]
        
        S_A[Service A]
        S_B[Service B]
        S_C{{<b>New Service C</b>}}
        
        Redis[(<b>Central Redis</b><br/>Shared Session)]
        Postgres[(<b>Auth DB</b><br/>User/Role Hub)]

        Auth --- Redis
        Auth --- Postgres
        
        Admin2((<b>Admin</b>)) ==>|<b>Single Login</b>| Auth
        
        Auth -.->|SSO Session| S_A
        Auth -.->|SSO Session| S_B
        Auth == "Immediate Connect" ==> S_C
    end

    style Auth fill:#f9f,stroke:#333,stroke-width:3px
    style S_C fill:#fff3e0,stroke:#e65100,stroke-width:2px,stroke-dasharray: 5 5
    style Redis fill:#e1f5fe,stroke:#01579b
```

---

### 🏗️ 핵심 설계 원칙 (Design Principles)

#### 1. 단일 진실의 원천 (Single Source of Truth)
- 모든 회원의 신원 확인(Identification)과 권한(Authorization)은 **Auth Server** 한 곳에서만 결정함.
- 개별 서비스는 별도의 회원 DB를 관리하지 않으며, Auth Server에서 제공하는 신원 정보를 기반으로 동작함.

#### 2. 무중단 SSO 세션 동기화 (Redis-based Session Clustering)
- 사용자가 한 번 로그인하면, 해당 세션 정보는 **Central Redis**에 저장되어 모든 연동 서비스가 실시간으로 공유함.
- 서버 장애나 재시작 시에도 Redis를 통해 인증 상태를 유지함으로써 관리자 경험의 단절을 방지함.

#### 3. 확장 가능한 표준 인터페이스 (Standardized OAuth2/OIDC)
- 서비스 연동 시 자체 인증 로직을 구현하지 않고, 표준 OAuth2/OIDC 인터페이스를 따름으로써 신규 서비스 도입 비용을 최소화함.

---

### 🔄 SSO 시퀀스 흐름 (Service A to Service B)

```mermaid
sequenceDiagram
    participant Admin as 관리자 (Admin)
    participant S_A as 서비스 A (Service A)
    participant Auth as 인증 서버 (Auth Server)
    participant S_B as 서비스 B (Service B)

    Note over Admin, S_A: [상황] 서비스 A 이용 중 업무 전환
    Admin->>S_B: 1. 서비스 B 접속 시도
    S_B->>Auth: 2. 유효한 SSO 세션 확인 (Silent Check)
    Auth-->>S_B: 3. 이미 인증된 관리자임을 확인 (OIDC ID Token)
    S_B-->>Admin: 4. 로그인 없이 즉시 서비스 B 메뉴 활성화
```

---

### 🛠️ 기술 스택 (Tech Stack)
- **Central Auth**: Spring Boot + Spring Security OAuth2
- **Session Hub**: Redis (Session Clustering)
- **Data Store**: PostgreSQL (Central Identity)
- **Client Protocol**: OAuth2 Client Library (Standard)

---

### 🔗 연관 자료
- **[Phase 3 Detail](./../phase/phase3.md)**: 단계별 상세 로드맵
- **[Design Notes](./../phase/PHASE3_DESIGN_NOTES.md)**: SSO 구현 시의 기술적 결정 사항
- **[SSO Implementation Test](../../src/test/java/com/pebble/baseAuth/config/oauth2/CustomOAuth2UserServiceTest.java)**: 실제 연동 검증 테스트 코드
