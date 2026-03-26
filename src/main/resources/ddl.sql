-- Users
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(30) NOT NULL,
    password VARCHAR(255) NOT NULL,
    -- [권한 시스템 도입 - Step 4] 
    -- 권한(Role) 정보를 저장하기 위한 컬럼을 추가합니다.
    -- VARCHAR로 저장하며, 'ROLE_USER', 'ROLE_ADMIN' 등의 문자열이 들어갑니다.
    role VARCHAR(20) NOT NULL,
    created_at TIMESTAMP(6) NOT NULL,
    updated_at TIMESTAMP(6) NOT NULL,
    deleted_at TIMESTAMP(6),
    CONSTRAINT uk_users_username UNIQUE (username)
);
