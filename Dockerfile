# Этап 1: Создание промежуточного образа с Docker, Maven и Java 17
FROM docker:24.0.5 as build

# Установка Java 17, Maven и Git
RUN apk add --no-cache openjdk17 maven git

# Настраиваем рабочую директорию
WORKDIR /app

# Копируем файлы проекта
COPY pom.xml .
COPY src ./src

# Копирование шаблонов и изображений
COPY src/main/resources/templates /app/templates
COPY src/main/webapp/WEB-INF/images /app/images

# Загружаем зависимости для Maven
RUN mvn dependency:go-offline

# Указываем команду по умолчанию для запуска тестов и сборки JAR
CMD ["mvn", "clean", "verify"]

## Этап 2: Создание окончательного Docker образа на основе легковесного Amazon Corretto
#FROM amazoncorretto:21-alpine-full
#
## Настраиваем рабочую директорию
#WORKDIR /app
#
## Копирование шаблонов и изображений
#COPY src/main/resources/templates /app/templates
#COPY src/main/webapp/WEB-INF/images /app/images
#
## Установка wget
#RUN apk update && \
#    apk add --no-cache wget && \
#    rm -rf /var/lib/apt/lists/*
#
## Установка Dockerize для ожидания внешних сервисов
#ENV DOCKERIZE_VERSION=v0.6.1
#RUN wget https://github.com/jwilder/dockerize/releases/download/$DOCKERIZE_VERSION/dockerize-linux-amd64-$DOCKERIZE_VERSION.tar.gz \
#    && tar -C /usr/local/bin -xzvf dockerize-linux-amd64-$DOCKERIZE_VERSION.tar.gz \
#    && rm dockerize-linux-amd64-$DOCKERIZE_VERSION.tar.gz
#
## Диагностика для проверки наличия JAR-файла
#RUN echo "Список файлов в /app/target после сборки:" && ls -l /app/target
#
## Копирование собранного JAR файла из этапа сборки
#COPY --from=build /app/target/*.jar /app/Quest_project-1.0.jar
#
## Запуск приложения с использованием Dockerize для ожидания базы данных
#ENTRYPOINT ["dockerize", "-wait", "tcp://quest-postgres-db:5432", "-timeout", "60s", "java", "-jar", "Quest_project-1.0.jar"]






























## Используем официальный образ Docker, который позволяет запускать Docker внутри Docker-контейнера
#FROM docker:24.0.5
#
## Устанавливаем Java (например, OpenJDK 17) и Maven
#RUN apk add --no-cache openjdk17 maven git
#
## Настраиваем рабочую директорию
#WORKDIR /app
#
#
#
## Копируем pom.xml и исходный код в контейнер
#COPY pom.xml .
#COPY src ./src
#
## Копирование шаблонов и изображений
#COPY src/main/resources/templates /app/templates
#COPY src/main/webapp/WEB-INF/images /app/images
#
## Загружаем зависимости для Maven
#RUN mvn dependency:go-offline
#
## Запускаем тесты
#CMD ["mvn", "verify"]











## Этап 1: Сборка приложения с использованием Maven
#FROM maven:3.9.6-eclipse-temurin-21-jammy as build
#WORKDIR /app
#COPY pom.xml .
#COPY src ./src
##RUN mvn clean package
#RUN mvn clean verify
#
## Этап 2: Создание окончательного Docker образа
#FROM amazoncorretto:21-alpine-full
#WORKDIR /app
#
## Копирование шаблонов и изображений
#COPY src/main/resources/templates /app/templates
#COPY src/main/webapp/WEB-INF/images /app/images
#
## Установка wget
#RUN apk update && \
#    apk add --no-cache wget && \
#    rm -rf /var/lib/apt/lists/*
#
## Установка Dockerize
#ENV DOCKERIZE_VERSION v0.6.1
#RUN wget https://github.com/jwilder/dockerize/releases/download/$DOCKERIZE_VERSION/dockerize-linux-amd64-$DOCKERIZE_VERSION.tar.gz \
#    && tar -C /usr/local/bin -xzvf dockerize-linux-amd64-$DOCKERIZE_VERSION.tar.gz \
#    && rm dockerize-linux-amd64-$DOCKERIZE_VERSION.tar.gz
#
## Копирование собранного JAR файла из этапа сборки
#COPY --from=build /app/target/*.jar Quest_project-1.0.jar
#
## Запуск с использованием Dockerize для ожидания базы данных
#ENTRYPOINT ["dockerize", "-wait", "tcp://quest-postgres-db:5432", "-timeout", "60s", "java", "-jar", "Quest_project-1.0.jar"]