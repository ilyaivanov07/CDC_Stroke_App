FROM openjdk:latest

ENV BOOTAPP_USR bootapp
ENV BOOTAPP_GROUP bootapp_group
ENV BOOTAPP_PATH /app.jar

COPY target/app.jar $BOOTAPP_PATH

# can't run psql on this image because psql is not available here
#COPY ./wait-for-strokeapp.sh /wait-for-strokeapp.sh

RUN groupadd -r $BOOTAPP_GROUP && useradd -r -g $BOOTAPP_GROUP $BOOTAPP_USR 
RUN chmod 777 $BOOTAPP_PATH
#RUN chmod +x /wait-for-strokeapp.sh

EXPOSE 8888
VOLUME /tmp
VOLUME /data

#RUN /wait-for-strokeapp.sh 
#ENTRYPOINT /wait-for-strokeapp.sh strokeapp_db

CMD "java" "-jar" "/app.jar"


