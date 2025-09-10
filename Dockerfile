FROM gradle:8.5.0-jdk17-alpine AS build

WORKDIR /app

COPY build.gradle settings.gradle main.gradle gradle.properties ./
COPY gradle ./gradle
COPY applications ./applications
COPY domain ./domain
COPY infrastructure ./infrastructure

RUN gradle clean build -x test --no-daemon -x validateStructure

FROM eclipse-temurin:17-jre-alpine

RUN adduser -D appuser

COPY --from=build /app/applications/app-service/build/libs/*.jar app.jar

VOLUME /tmp

USER appuser

EXPOSE 8086

ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=70 -Djava.security.egd=file:/dev/./urandom"

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]