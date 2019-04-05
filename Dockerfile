FROM java:8u111-jre
COPY target/*.jar ask-0.0.1-SNAPSHOT.jar
ENTRYPOINT ["java","-jar","/ask-0.0.1-SNAPSHOT.jar"]