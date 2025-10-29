FROM openjdk:21-jdk-slim

WORKDIR /app

COPY . .

# Build using Gradle
RUN chmod +x ./gradlew && ./gradlew clean build -x test

# Run the application - use the executable JAR (the larger one)
CMD ["java", "-jar", "build/libs/springboot-demo-0.0.1-SNAPSHOT.jar"]
