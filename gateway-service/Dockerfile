# Render Deployment Dockerfile for Gateway Service
# Pre-requisite: Run './gradlew :gateway-service:bootJar' locally before building the image
FROM eclipse-temurin:21-jre-jammy

WORKDIR /app

# Copy the pre-built JAR from the build/libs directory
# Ensure the JAR name matches your project version (e.g., 0.0.1-SNAPSHOT)
COPY build/libs/*.jar app.jar

# Expose the internal port (Render will map this to the public internet)
EXPOSE 8000

# Run the application with production profile
# -Dserver.port=${PORT:8000} ensures it listens on Render's dynamic port
ENTRYPOINT ["java", "-Dserver.port=${PORT:8000}", "-Dspring.profiles.active=prod", "-jar", "app.jar"]
