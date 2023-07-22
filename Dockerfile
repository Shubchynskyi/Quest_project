FROM openjdk:17-jdk-alpine
MAINTAINER d.shubchynskyi@gmail.com
COPY target/Quest_project-1.0-SNAPSHOT.jar Quest_project-1.0-SNAPSHOT.jar
ENTRYPOINT ["java", "-jar", "/Quest_project-1.0-SNAPSHOT.jar"]
