# Etapa 1: Compilar el proyecto con Maven
FROM maven:3.9.6-eclipse-temurin AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# Etapa 2: Ejecutar el .jar con Java
FROM eclipse-temurin:21-jdk
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

EXPOSE 1314

# Ejecutamos pasando el puerto dinámico a Spring Boot
ENTRYPOINT ["java", "-jar", "app.jar", "--server.port=${PORT:-1314}"]
