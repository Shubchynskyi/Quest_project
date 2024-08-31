# Этап 1: Сборка приложения с использованием Maven
FROM maven:3.9.6-eclipse-temurin-21-jammy as build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Этап 2: Создание окончательного Docker образа
FROM amazoncorretto:21-alpine-full
WORKDIR /app

# Копирование шаблонов и изображений
COPY src/main/resources/templates /app/templates
COPY src/main/webapp/WEB-INF/images /app/images

# Установка wget
RUN apk update && \
    apk add --no-cache wget && \
    rm -rf /var/lib/apt/lists/*

# Установка Dockerize
ENV DOCKERIZE_VERSION v0.6.1
RUN wget https://github.com/jwilder/dockerize/releases/download/$DOCKERIZE_VERSION/dockerize-linux-amd64-$DOCKERIZE_VERSION.tar.gz \
    && tar -C /usr/local/bin -xzvf dockerize-linux-amd64-$DOCKERIZE_VERSION.tar.gz \
    && rm dockerize-linux-amd64-$DOCKERIZE_VERSION.tar.gz

# Копирование собранного JAR файла из этапа сборки
COPY --from=build /app/target/*.jar Quest_project-1.0.jar

# Запуск с использованием Dockerize для ожидания базы данных
ENTRYPOINT ["dockerize", "-wait", "tcp://quest-postgres-db:5432", "-timeout", "60s", "java", "-jar", "Quest_project-1.0.jar"]