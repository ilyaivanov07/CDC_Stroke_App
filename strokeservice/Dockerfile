FROM openjdk:latest

ENV BOOTAPP_USR bootapp
ENV BOOTAPP_GROUP bootapp_group
ENV BOOTAPP_PATH /app.jar
ENV BOOTAPP_DATA_VOLUME /data

COPY target/app.jar $BOOTAPP_PATH

RUN groupadd -r $BOOTAPP_GROUP && useradd -r -g $BOOTAPP_GROUP $BOOTAPP_USR 
RUN chmod 777 $BOOTAPP_PATH

EXPOSE 8888

VOLUME /tmp
VOLUME $BOOTAPP_DATA_VOLUME

CMD "java" "-jar" "/app.jar"

