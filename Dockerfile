# Etapa 1: construir el proyecto
FROM maven:3.9.6-eclipse-temurin-17 AS builder

ENV LANG=C.UTF-8 LC_ALL=C.UTF-8 MAVEN_OPTS="-Dfile.encoding=UTF-8"

WORKDIR /app
COPY . .

# Compila sin tests para reducir uso de memoria (forzando UTF-8)
RUN mvn -Dfile.encoding=UTF-8 clean package -DskipTests

# Etapa 2: imagen final para ejecutar
FROM eclipse-temurin:17-jdk

WORKDIR /app
COPY --from=builder /app/target/backend-0.0.1-SNAPSHOT.jar app.jar

ENV PORT=8080
EXPOSE ${PORT}

CMD ["java", "-Xmx256m", "-Dfile.encoding=UTF-8", "-jar", "app.jar"]
