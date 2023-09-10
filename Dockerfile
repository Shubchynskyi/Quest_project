FROM openjdk:17-jdk
MAINTAINER d.shubchynskyi@gmail.com
COPY target/Quest_project-1.0.jar Quest_project-1.0.jar
ENTRYPOINT ["java", "-jar", "/Quest_project-1.0.jar"]
