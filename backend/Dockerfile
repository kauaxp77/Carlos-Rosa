# Stage 1: Build the application
FROM maven:3.9.6-eclipse-temurin-21-jammy AS build
WORKDIR /app
COPY backend/pom.xml .
# Try to download dependencies to cache them (might fail if pom is missing things but it's okay for scaffold)
RUN mvn dependency:go-offline -B || true
COPY backend/src ./src
# We use -DskipTests because this is a scaffold and tests might not be set up yet
RUN mvn clean package -DskipTests

# Stage 2: Run the application
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
COPY database/migrations/ /app/migrations/
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
