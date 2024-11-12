# Use OpenJDK as the base image for Java 21
FROM openjdk:21-jdk-slim AS builder

# Set the JAVA_HOME environment variable to the correct path for OpenJDK
# The path may vary; you can check it by running: docker run --rm openjdk:21-jdk-slim bash -c 'echo $JAVA_HOME'
ENV JAVA_HOME=/usr/local/openjdk-21
ENV PATH="$JAVA_HOME/bin:$PATH"

# Set the working directory inside the container
WORKDIR /app

# Install necessary utilities like wget and tar for Maven installation
RUN apt-get update && apt-get install -y wget tar && rm -rf /var/lib/apt/lists/*

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
FROM openjdk:21-jdk-slim

# Set the working directory for the runtime container
WORKDIR /app

# Copy the JAR file from the builder stage to the runtime stage
COPY --from=builder /app/target/tac-case-api-service-*.jar /app/tac-case-api-service.jar

# Expose the port the application runs on
EXPOSE 8080

# Define the command to run the application
ENTRYPOINT ["java", "-jar", "/app/client-api-demo.jar"]
