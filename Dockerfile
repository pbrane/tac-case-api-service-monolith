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
FROM eclipse-temurin:21-jdk-noble

#The Maven container-build profile sets the final name of the JAR to "app.jar"
COPY --from=builder /app/target/app.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
