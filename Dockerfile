FROM amazonlinux AS corretto
RUN rpm --import https://yum.corretto.aws/corretto.key && \
    curl -L -o /etc/yum.repos.d/corretto.repo https://yum.corretto.aws/corretto.repo && \
    dnf install -y java-20-amazon-corretto-devel findutils fontconfig && \
    dnf clean all
ENV JAVA_HOME=/usr/lib/jvm/java-20-amazon-corretto

FROM corretto AS build
RUN dnf install -y git
COPY . /home/gradle/project
WORKDIR /home/gradle/project
RUN ./gradlew installDist --no-daemon

FROM corretto
EXPOSE 41523
WORKDIR /opt/visualize
COPY --from=build /home/gradle/project/build/install/* .
ENTRYPOINT ["./bin/visualize"]
