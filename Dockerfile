FROM gradle:jdk8-alpine AS TEMP_BUILD_IMAGE
USER root
ENV APP_HOME=/app/
WORKDIR $APP_HOME
COPY . $APP_HOME
RUN gradle build --no-daemon --console plain || return 0
#
FROM openjdk:8-jre-alpine

ENV ARTIFACT_NAME=ftp-websocket-server.jar
ENV FTP_REDIS_HOST=localhost
ENV FTP_DB_HOST=localhost
ENV FTP_DB_PORT=5432
ENV FTP_DB_NAME=ftp_server
ENV FTP_DB_SCHEMA=public
ENV FTP_DB_SSL=false
ENV FTP_DB_USER=ftp_user
ENV FTP_DB_PASSWORD=ribamech
ENV JAVA_OPTS="-server -Xms1g -Xmx1g -XX:NewSize=1g -XX:+UseConcMarkSweepGC -XX:+UseParNewGC -XX:+UseStringDeduplication"
ENV JAVA_HOME /usr/lib/jvm/java-1.8-openjdk

ENV APP_HOME=/app/
WORKDIR $APP_HOME
COPY --from=TEMP_BUILD_IMAGE $APP_HOME/build/libs/ftp-websocket-server.jar .
EXPOSE 8443
ENTRYPOINT java $JAVA_OPTS -jar /app/$ARTIFACT_NAME

