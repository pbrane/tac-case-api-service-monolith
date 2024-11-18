# Set the base image
FROM eclipse-temurin:21-jdk-noble AS builder

# Set the working directory
WORKDIR /app

# Copy Maven wrapper and configuration
COPY mvnw .
COPY .mvn .mvn

# Copy the project files
COPY pom.xml .
COPY src ./src

# Ensure the Maven wrapper script is executable
RUN chmod +x mvnw

# Build the application
RUN ./mvnw clean package -DskipTests -P container-build

# Final image
#FROM openjdk:21-jdk-slim
FROM eclipse-temurin:21-jdk-noble
WORKDIR /app
COPY --from=builder /app/target/tac-case-api-service-*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]