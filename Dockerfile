FROM eclipse-temurin:21.0.8_9-jdk-jammy AS builder
WORKDIR /opt/app
COPY ./gradle ./gradle
COPY ./gradlew ./
COPY ./build.gradle.kts ./
COPY ./gradle.properties ./
COPY ./settings.gradle.kts ./
COPY ./src ./src
RUN ./gradlew buildFatJar

FROM eclipse-temurin:21.0.8_9-jre-jammy AS final
WORKDIR /opt/app
EXPOSE 8080
COPY --from=builder /opt/app/build/libs/*-all.jar /opt/app/announcer.jar
ENTRYPOINT ["java", "-jar", "/opt/app/announcer.jar"]