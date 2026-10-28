FROM eclipse-temurin:21-jdk AS build
WORKDIR /app
COPY gradlew settings.gradle.kts build.gradle.kts gradle.properties ./
COPY gradle gradle
COPY content content
COPY src src
RUN ./gradlew --no-daemon wasmJsBrowserDistribution

FROM caddy:2-alpine
COPY Caddyfile /etc/caddy/Caddyfile
COPY --from=build /app/build/dist/wasmJs/productionExecutable /srv
