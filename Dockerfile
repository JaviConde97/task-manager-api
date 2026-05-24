# ===========================================
# Dockerfile multi-stage para task-manager-api
# ===========================================
# Stage 1 (builder): compila el proyecto con Maven
# Stage 2 (runtime): imagen final ligera solo con el JAR
# Resultado: imagen mucho más pequeña que si se hiciera en un solo stage

# --- Stage 1: Construcción ---
FROM eclipse-temurin:21-jdk-alpine AS builder

WORKDIR /build

# Copiamos primero solo el pom.xml para aprovechar la caché de Docker
# Si el código cambia pero las dependencias no, Maven no las descarga otra vez
COPY pom.xml .
COPY .mvn/ .mvn/
COPY mvnw .

# Descargamos las dependencias (cacheado por Docker si pom.xml no cambia)
RUN ./mvnw dependency:go-offline -q

# Ahora copiamos el código fuente y construimos
COPY src/ src/
RUN ./mvnw package -DskipTests -q

# --- Stage 2: Imagen final ---
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Copiamos solo el JAR del stage anterior
COPY --from=builder /build/target/*.jar app.jar

# Puerto que expone la aplicación
EXPOSE 8080

# Arranque de la aplicación
ENTRYPOINT ["java", "-jar", "app.jar"]
