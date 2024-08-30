# Этап 1: Сборка приложения с использованием Maven
FROM maven:3.9.6-eclipse-temurin-21-jammy as build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package

# Этап 2: Создание окончательного Docker образа
FROM amazoncorretto:21-alpine-full
WORKDIR /app

# Установка Dockerize для ожидания зависимостей
ENV DOCKERIZE_VERSION v0.6.1
RUN apt-get update && apt-get install -y wget && \
    wget https://github.com/jwilder/dockerize/releases/download/$DOCKERIZE_VERSION/dockerize-linux-amd64-$DOCKERIZE_VERSION.tar.gz \
    && tar -C /usr/local/bin -xzvf dockerize-linux-amd64-$DOCKERIZE_VERSION.tar.gz \
    && rm dockerize-linux-amd64-$DOCKERIZE_VERSION.tar.gz

# Копирование собранного JAR файла из этапа сборки
COPY --from=build /app/target/*.jar Quest_project-1.0.jar

# Запуск с использованием Dockerize для ожидания базы данных
ENTRYPOINT ["dockerize", "-wait", "tcp://quest-postgres-db:5432", "-timeout", "60s", "java", "-jar", "Quest_project-1.0.jar"]







#FROM openjdk:17-jdk
#MAINTAINER d.shubchynskyi@gmail.com
#COPY target/Quest_project-1.0.jar Quest_project-1.0.jar
#ENTRYPOINT ["java", "-jar", "/Quest_project-1.0.jar"]
