FROM openjdk:18.0.2.1-jdk-slim-buster
MAINTAINER Tim Sokollek (tim.sokollek@ipa.fraunhofer.de)

RUN apt update && \
    apt install -y curl jq


COPY src/main/docker/startup.sh /app/startup.sh
RUN chmod +x /app/startup.sh

COPY target/aas.camera-0.0.1-SNAPSHOT-jar-with-dependencies.jar /app/app.jar

WORKDIR /app

ENTRYPOINT ["/bin/bash", "-c", "/app/startup.sh"]
