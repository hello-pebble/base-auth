# Render Deployment Dockerfile for Gateway Service (Multi-Stage v2)
# This Dockerfile expects to be built from the ROOT directory of the project
# Command: docker build -f gateway-service/Dockerfile .

# Build Stage
FROM eclipse-temurin:21-jdk-jammy AS build
WORKDIR /app

# Copy the Gradle wrapper and project files
COPY gradlew .
COPY gradle gradle
COPY build.gradle.kts .
COPY settings.gradle.kts .

# Copy only the gateway-service and its dependencies
COPY gateway-service gateway-service

# Run the bootJar task for gateway-service
# Use --no-daemon to save memory on Render's free tier
RUN ./gradlew :gateway-service:bootJar --no-daemon

# Run Stage
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

# Copy the generated JAR from the build stage
COPY --from=build /app/gateway-service/build/libs/*.jar app.jar

# Expose the internal port (Render will map this to the public internet)
EXPOSE 8000

# Run the application with production profile
# -Dserver.port=${PORT:8000} ensures it listens on Render's dynamic port
ENTRYPOINT ["java", "-Dserver.port=${PORT:8000}", "-Dspring.profiles.active=prod", "-jar", "app.jar"]
