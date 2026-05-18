# ============================================================
# ETAPA 1 - BUILD: Compila el JAR con Maven
# ============================================================
FROM maven:3.9.6-eclipse-temurin-21-alpine AS builder

WORKDIR /app

# Copiar PRIMERO el pom.xml para aprovechar el cache de capas:
# Si el código fuente cambia pero las dependencias (pom.xml) no,
# Docker reutiliza la capa de descarga de dependencias (~200MB ahorrados)
COPY pom.xml .

# Descargar todas las dependencias en modo offline
# -B = modo batch (sin colores, ideal para CI/CD)
RUN mvn dependency:go-offline -B

# Ahora copiar el código fuente
COPY src ./src

# Compilar el proyecto y generar el JAR ejecutable
# -DskipTests: los tests se ejecutan en el pipeline de CI, no aquí
# -B: modo batch para logs limpios
RUN mvn clean package -DskipTests -B

# ============================================================
# ETAPA 2 - PRODUCCIÓN: Solo el JRE (sin Maven, sin JDK completo)
# La imagen final NO incluye Maven ni el código fuente
# Resultado: ~200MB en vez de ~600MB
# ============================================================
FROM eclipse-temurin:21-jre-alpine AS production

WORKDIR /app

# Crear usuario sin privilegios root (seguridad: mínimo privilegio)
RUN addgroup -S appgroup && adduser -S appuser -G appgroup

# Copiar SOLO el JAR generado desde la etapa builder
COPY --from=builder /app/target/*.jar app.jar

# Ajustar permisos
RUN chown appuser:appgroup app.jar

# Cambiar al usuario no-root
USER appuser

# La app escucha en el puerto 8081 (definido en application.properties)
EXPOSE 8081

# Variables de entorno con valores por defecto
# Se sobreescriben en docker-compose.yml o en el pipeline CI/CD
ENV DB_ENDPOINT=localhost \
    DB_PORT=3306 \
    DB_NAME=despachos_db \
    DB_USERNAME=root \
    DB_PASSWORD=root

# Iniciar la aplicación Spring Boot
# -Xmx512m: límite de memoria heap (importante en t2.micro de AWS)
CMD ["java", "-Xmx512m", "-jar", "app.jar"]
