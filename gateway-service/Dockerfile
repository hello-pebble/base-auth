# Build Stage
FROM eclipse-temurin:21-jdk-jammy AS build
WORKDIR /app

# 1. Gradle Wrapper와 설정 파일 복사
COPY gradlew .
COPY gradle gradle
COPY build.gradle.kts .
COPY settings.gradle.kts .

# 2. 소스 코드 복사 (전체 프로젝트 루트 기준)
COPY gateway-service gateway-service

# 3. 빌드 권한 부여 및 빌드 수행
RUN chmod +x gradlew && ./gradlew :gateway-service:bootJar --no-daemon

# Run Stage
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

# 4. 빌드 스테이지에서 생성된 JAR 복사
COPY --from=build /app/gateway-service/build/libs/*.jar app.jar

EXPOSE 8000

# 5. 실행 설정
ENTRYPOINT ["java", "-Dserver.port=${PORT:8000}", "-Dspring.profiles.active=prod", "-jar", "app.jar"]
