
FROM openjdk:11
VOLUME /data/docker
ARG JAVA_OPTS
ENV JAVA_OPTS=$JAVA_OPTS
ADD mee-manage.jar mee-manage
EXPOSE 8801
ENTRYPOINT exec java $JAVA_OPTS -jar mee-manage.jar
# For Spring-Boot project, use the entrypoint below to reduce Tomcat startup time.
#ENTRYPOINT exec java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar mee-manage.jar
