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

# Heroku will provide $PORT; we will define port 8080 here, so when Docker builds and runs, it knows what port to use
ENV PORT=8080

EXPOSE 8080

# Run the application // changed to below so we can build and run with Docker in a way Docker understands
CMD java -Dserver.port=$PORT -jar build/libs/demo-0.0.1-SNAPSHOT.jar
# what we have before is below
# CMD ["java", "-Dserver.port=$PORT", "-jar", "build/libs/demo-0.0.1-SNAPSHOT.jar"]