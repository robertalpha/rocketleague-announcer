### Builder
FROM eclipse-temurin:21.0.8_9-jdk-jammy AS builder

WORKDIR /opt/app
COPY gradle ./gradle
COPY src ./src
COPY gradlew build.gradle.kts gradle.properties settings.gradle.kts ./

RUN ./gradlew buildFatJar

### Runner
FROM eclipse-temurin:21-jre-alpine AS final

RUN apk --no-cache add mosquitto supervisor

COPY --from=builder /opt/app/build/libs/*-all.jar /opt/app/announcer.jar
COPY docker/supervisord.conf docker/mosquitto.conf /etc/

# announcer port
EXPOSE 8080
# MQTT port
EXPOSE 1883

CMD ["/usr/bin/supervisord", "--nodaemon", "--configuration", "/etc/supervisord.conf"]