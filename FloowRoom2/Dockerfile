# ── Stage 1: Build ──────────────────────────────────────────
FROM maven:3.9.6-eclipse-temurin-17-alpine AS build
WORKDIR /app

# Copia pom primeiro para aproveitar cache de dependências
COPY pom.xml .
RUN mvn dependency:go-offline -q

# Copia o código e compila
COPY src ./src
RUN mvn clean package -DskipTests -q

# ── Stage 2: Runtime ────────────────────────────────────────
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Cria usuário não-root por segurança
RUN addgroup -S floowroom && adduser -S floowroom -G floowroom
USER floowroom

COPY --from=build /app/target/floowroom-backend-1.0.0.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", \
  "-XX:+UseContainerSupport", \
  "-XX:MaxRAMPercentage=75.0", \
  "-Djava.security.egd=file:/dev/./urandom", \
  "-jar", "app.jar"]
