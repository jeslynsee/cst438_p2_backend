# Use OpenJDK 17 as base image
FROM openjdk:17-jdk-slim

# Set working directory
WORKDIR /app

# Copy gradle wrapper and build files
COPY gradlew .
COPY gradle gradle
COPY build.gradle.kts .
COPY settings.gradle.kts .

# Copy source code
COPY src src

# Make gradlew executable
RUN chmod +x ./gradlew

# Build the application
RUN ./gradlew build -x test

# Expose port (Heroku will set the PORT env variable)
EXPOSE $PORT

# Run the application
CMD ["java", "-Dserver.port=$PORT", "-jar", "build/libs/demo-0.0.1-SNAPSHOT.jar"]