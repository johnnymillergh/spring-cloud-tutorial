# Best practices for writing Dockerfiles https://docs.docker.com/develop/develop-images/dockerfile_best-practices/
FROM java:8-jre
MAINTAINER Johnny Miller <johnnysviva@outlook.com>
VOLUME /tmp
ARG JAR_FILE
RUN echo ${JAR_FILE}
ADD ${JAR_FILE} app.jar
ENTRYPOINT ["java", "-Xmx200m", "-jar", "app.jar"]
# Debugging the application in a Docker container
# ENTRYPOINT ["java", "-Xmx200m", "-Xdebug", "-Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=XXXX", "-jar", "app.jar"]
EXPOSE 8766
