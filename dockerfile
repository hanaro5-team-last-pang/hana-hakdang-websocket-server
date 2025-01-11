FROM openjdk:17.0.2-slim-buster as base
FROM base as builder
WORKDIR /server

COPY . .

RUN ./gradlew build


FROM base as runner
WORKDIR /server

COPY --from=builder /server/build/libs/hana-hakdang-websocket-server-0.0.1-SNAPSHOT.jar ./server.jar

COPY .env.prod ./.env

EXPOSE 8080