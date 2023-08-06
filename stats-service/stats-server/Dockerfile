FROM amazoncorretto:11-alpine-jdk
#ENV JAVA_TOOL_OPTIONS -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:9091
COPY target/*.jar stats-server.jar
#EXPOSE 9090
ENTRYPOINT ["java", "-jar", "/stats-server.jar"]