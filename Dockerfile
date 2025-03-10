# Stage 1: Build the application
FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Create a lightweight runtime image
FROM eclipse-temurin:17-jre AS runtime
WORKDIR /app
COPY --from=build /app/target/jenkinstest-0.0.1-SNAPSHOT.jar app.jar
CMD ["java", "-jar", "app.jar"]
