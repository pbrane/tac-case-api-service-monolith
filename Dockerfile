# Use OpenJDK as the base image for Java 21
#FROM openjdk:21-jdk-slim AS builder
#Swiched to Elicpse Temurin
FROM eclipse-temurin:21-jdk-noble AS builder

# Set the working directory inside the container
WORKDIR /app

# Download and install Maven separately to ensure mvnw can work
RUN wget -qO- https://downloads.apache.org/maven/maven-3/3.9.9/binaries/apache-maven-3.9.9-bin.tar.gz | tar xz -C /opt \
    && ln -s /opt/apache-maven-3.9.9 /opt/maven

# Set Maven environment variables
ENV MAVEN_HOME=/opt/maven
ENV PATH="$MAVEN_HOME/bin:$PATH"

# Copy the Maven wrapper and configuration files to the container
COPY pom.xml mvnw ./
COPY .mvn .mvn

# Run Maven to download dependencies
RUN mvn dependency:go-offline

# Copy the source code to the container
COPY src src

# Build the application
RUN mvn clean package -DskipTests -P container-build

# Use a minimal runtime image for the final stage
FROM eclipse-temurin:21-jdk-noble

# Set the working directory for the runtime container
WORKDIR /app

# Copy the JAR file from the builder stage to the runtime stage
COPY --from=builder /app/target/tac-case-api-service-*.jar /app/app.jar

# Define the command to run the application
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
