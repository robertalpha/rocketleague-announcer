### Builder
FROM eclipse-temurin:21.0.8_9-jdk-jammy AS builder

ARG GITHUB_ACTOR
ARG GITHUB_TOKEN

WORKDIR /opt/app
COPY gradle ./gradle
COPY src ./src
COPY gradlew build.gradle.kts gradle.properties settings.gradle.kts ./

RUN ./gradlew buildFatJar

### Runner
FROM eclipse-temurin:21-jre-alpine AS final

COPY --from=builder /opt/app/build/libs/*-all.jar /opt/app/announcer.jar

# announcer port
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/opt/app/announcer.jar"]
