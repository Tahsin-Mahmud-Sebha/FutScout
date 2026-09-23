# --- Build stage ---
FROM maven:3.9-eclipse-temurin-25 AS build
WORKDIR /app

# Copy only the pom first so Docker can cache dependency downloads
COPY pom.xml .
RUN mvn -B dependency:go-offline

# Now copy the source and build
COPY src ./src
RUN mvn -B clean package -DskipTests

# --- Run stage ---
FROM eclipse-temurin:25-jre-alpine
WORKDIR /app

# Render sets PORT at runtime; your application.properties already reads it
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
