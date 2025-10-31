# syntax=docker/dockerfile:1.7

FROM maven:3.9.9-eclipse-temurin-17 AS builder

WORKDIR /app

COPY pom.xml .
COPY src ./src

RUN mvn -q -DskipTests clean package

FROM eclipse-temurin:17-jre-jammy

RUN apt-get update \
 && apt-get install -y --no-install-recommends curl \
 && rm -rf /var/lib/apt/lists/*

RUN groupadd -r spring && useradd -r -g spring spring

WORKDIR /app

COPY --from=builder /app/target/*.jar /app/app.jar

EXPOSE 8086

ENTRYPOINT ["sh","-c","exec java $JAVA_TOOL_OPTIONS -jar /app/app.jar"]
