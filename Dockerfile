# ==============================================================================
# CareerFit AI - Multi-Stage Dockerfile for Spring Boot 3 & Java 21 Runtime
# ==============================================================================

# Stage 1: Build the Maven application
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app

# Cache Maven dependencies
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy application source code and compile
COPY src ./src
RUN mvn clean package -DskipTests -B

# Stage 2: Minimal, secure Java 21 runtime container
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Add non-root system user for production container security
RUN addgroup -S appgroup && adduser -S appuser -G appgroup
USER appuser

# Copy executable jar from builder stage
COPY --from=build /app/target/careerfitai-backend-*.jar app.jar

# Render injects PORT dynamically (defaults to 8080)
ENV PORT=8080
EXPOSE ${PORT}

# JVM parameters optimized for containerized environments
ENTRYPOINT ["sh", "-c", "java -Dserver.port=${PORT:-8080} -Djava.security.egd=file:/dev/./urandom -jar app.jar"]
