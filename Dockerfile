# Stage 1: Build
FROM maven:3.9.9-eclipse-temurin-21 AS build
# Note: Even Maven 21 images can't target 25.
# To build 25, you would need a Maven image with JDK 25.
FROM maven:3.9.12-eclipse-temurin-25-alpine AS build_stage
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# Stage 2: Run
FROM eclipse-temurin:25-alpine
WORKDIR /app
COPY --from=build_stage /app/target/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
