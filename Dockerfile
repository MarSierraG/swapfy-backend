# Etapa 1: build del proyecto
FROM maven:3.9.2-eclipse-temurin-22 AS builder
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# Etapa 2: ejecución
FROM eclipse-temurin:22-jdk
WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
