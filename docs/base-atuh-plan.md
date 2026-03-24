# Plan: Base Auth Extraction (Phase 1)

This plan details the steps to extract a minimal `base-auth` service from the existing SNS project. The goal is to isolate the authentication logic (Login/Signup) while removing all SNS-specific features.

## 1. Objective
Create a standalone authentication service (`base-auth`) by stripping down the existing codebase.
- **Focus:** User Account Management (Signup, Login, Session Check).
- **Tech Stack:** Java 25, Spring Boot, Spring Security (Session/Redis), PostgreSQL.
- **Scope:** Remove all dependencies on `Post`, `Follow`, `Profile`, `Timeline`, etc.

## 2. Key Changes

### 2.1. Dependency Cleanup (`build.gradle.kts`)
- Remove `software.amazon.awssdk:s3` and its BOM.
- Keep `spring-session-data-redis` for session management.
- Keep `postgresql` and `h2`.
- Keep `spring-boot-starter-security`, `spring-boot-starter-web`, `spring-boot-starter-data-jpa`, `spring-boot-starter-validation`.

### 2.2. Configuration Cleanup (`application.yaml`)
- Remove `post`, `timeline`, `storage` configuration blocks.
- Rename application to `base-auth`.
- Keep `spring.datasource`, `spring.data.redis`, `spring.jpa`.

### 2.3. Database Schema (`ddl.sql`)
- Remove `follows`, `follow_counts` tables.
- Keep `users` table.

### 2.4. Java Code Removal
Delete the following packages and files:
- **Controllers:** `FollowController`, `LikeController`, `MediaController`, `PostController`, `ProfileController`, `QuoteController`, `ReplyController`, `RepostController`, `TimelineController`.
- **Services:** `FollowService`, `LikeService`, `MediaService`, `PostService`, `ProfileService`, `QuoteService`, `ReplyService`, `RepostService`, `TimelineService`, `StorageService`.
- **Repositories:** Associated repositories.
- **DTOs:** All except `LoginRequest`, `UserSignUpRequest`, `UserResponse`.
- **Config:** `S3Config`, `StorageConfig`, `PostViewConfig`, `TimelineConfig`, `SchedulingConfig`.
- **Scheduler:** `PostViewScheduler`.
- **Domain Entities:** All except `User`, `BaseEntity`, `UserException`, `DomainException`.

### 2.5. Java Code Refactoring
- **`UserService.java`**: Remove `profileService` dependency. The `signUp` method currently initializes a profile; this logic must be removed.
- **`UserController.java`**: Remove `findAll` endpoint. Keep `signup`, `login`, and `me` (for session verification).
- **`UserResponse.java`**: Verify it only contains user basics (id, username).

## 3. Implementation Steps

1.  **Backup & Init**: Confirm current state.
2.  **Clean Dependencies**: Edit `build.gradle.kts`.
3.  **Clean Configuration**: Edit `application.yaml` and `ddl.sql`.
4.  **Remove Code**: Delete identified files and directories.
5.  **Refactor Core Logic**:
    - Modify `UserService` to remove `ProfileService` usage.
    - Modify `UserController` to remove unused endpoints.
6.  **Verify**: Ensure the application starts and basic auth endpoints work.

## 4. Verification Plan
- **Build**: `./gradlew clean build` should pass.
- **Endpoint Check**:
    - `POST /api/v1/users/signup` -> 201 Created
    - `POST /api/v1/login` -> 200 OK (with JSESSIONID)
    - `GET /api/v1/users/me` -> 200 OK (authenticated)
