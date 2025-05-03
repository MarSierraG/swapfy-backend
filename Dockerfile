# Etapa 1: construir el proyecto
FROM maven:3.9.6-eclipse-temurin-17 AS builder

WORKDIR /app
COPY . .

# Compila sin tests para reducir uso de memoria
RUN mvn clean package -DskipTests

# Etapa 2: imagen final para ejecutar
FROM eclipse-temurin:17-jdk

WORKDIR /app

# Copia el JAR generado desde la etapa anterior
COPY --from=builder /app/target/backend-0.0.1-SNAPSHOT.jar app.jar

# Puerto expuesto dinámicamente por Railway
ENV PORT=8080
EXPOSE ${PORT}

# Iniciar el backend
CMD ["java", "-Xmx256m", "-jar", "app.jar"]
