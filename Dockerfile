FROM openjdk:8-jre-alpine
ENV JAVA_HOME /usr/lib/jvm/java-1.8-openjdk
EXPOSE 8443
WORKDIR /app/

COPY /build/libs/ftp-websocket-server.war /app/ftp-websocket-server.war
ENTRYPOINT ["java", "-jar", "/app/ftp-websocket-server.war"]
