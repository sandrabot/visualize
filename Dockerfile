FROM ubuntu:rolling AS openjdk
RUN apt update && apt install -y curl openjdk-25-jdk-headless && rm -rf /var/lib/apt/lists/*

FROM openjdk AS build
WORKDIR /srv/visualize
COPY . .
RUN --mount=type=cache,target=/root/.gradle ./gradlew installDist --no-daemon

FROM openjdk AS app
EXPOSE 41523
WORKDIR /opt/visualize
COPY --from=build /srv/visualize/build/install/* .
ENTRYPOINT ["./bin/visualize"]
HEALTHCHECK --timeout=3s CMD curl -sf http://localhost:41523/status || exit 1
