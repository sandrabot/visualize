FROM amazonlinux AS corretto
RUN rpm --import https://yum.corretto.aws/corretto.key && \
    curl -L -o /etc/yum.repos.d/corretto.repo https://yum.corretto.aws/corretto.repo && \
    dnf install -y java-21-amazon-corretto-devel findutils fontconfig && \
    dnf clean all
ENV JAVA_HOME=/usr/lib/jvm/java-21-amazon-corretto

FROM corretto AS build
RUN dnf install -y git
COPY . /home/gradle/project
WORKDIR /home/gradle/project
RUN ./gradlew installDist --no-daemon

FROM corretto AS app
EXPOSE 41523
WORKDIR /opt/visualize
COPY --from=build /home/gradle/project/build/install/* .
ENTRYPOINT ["./bin/visualize"]
HEALTHCHECK --interval=5m --timeout=3s \
    CMD curl -sf http://localhost:41523/status || exit 1
