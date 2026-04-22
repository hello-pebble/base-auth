# Build Stage
FROM eclipse-temurin:21-jdk-jammy AS build
WORKDIR /app

COPY gradlew .
COPY gradle gradle
COPY build.gradle.kts .
COPY settings.gradle.kts .
COPY gateway-service gateway-service

RUN chmod +x gradlew && ./gradlew :gateway-service:bootJar --no-daemon

# Run Stage
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

COPY --from=build /app/gateway-service/build/libs/*.jar app.jar

EXPOSE 8000

# 쉘 형식으로 변경해야 환경변수 치환 가능
ENTRYPOINT ["sh", "-c", "java -Dserver.port=${PORT:-8000} -Dspring.profiles.active=prod -jar app.jar"]