FROM openjdk:8-jdk-alpine
VOLUME /tmp
COPY target/*.jar ask-0.0.1-SNAPSHOT.jar
ENTRYPOINT ["java","-jar","/ask-0.0.1-SNAPSHOT.jar"]