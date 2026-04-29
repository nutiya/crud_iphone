# ---- Build stage ----
FROM eclipse-temurin:17-jdk-alpine AS builder
WORKDIR /app

# Copy only files needed for dependencies
COPY mvnw pom.xml ./
COPY .mvn .mvn

RUN sed -i 's/\r//' mvnw && chmod +x mvnw

# ✅ Download dependencies first (cached layer)
RUN ./mvnw dependency:go-offline -B

# Now copy source code
COPY src ./src

# Build project
RUN ./mvnw package -DskipTests -B

# ---- Run stage ----
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

RUN addgroup -S spring && adduser -S spring -G spring
USER spring

COPY --from=builder /app/target/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]