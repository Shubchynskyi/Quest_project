# Этап 2: Создание окончательного Docker образа на основе легковесного Amazon Corretto
FROM amazoncorretto:21-alpine-full

# Настраиваем рабочую директорию
WORKDIR /app

# Копирование шаблонов и изображений
COPY src/main/resources/templates /app/templates
COPY src/main/webapp/WEB-INF/images /app/images

# Установка wget
RUN apk update && \
    apk add --no-cache wget && \
    rm -rf /var/lib/apt/lists/*

# Установка Dockerize для ожидания внешних сервисов
ENV DOCKERIZE_VERSION=v0.6.1
RUN wget https://github.com/jwilder/dockerize/releases/download/$DOCKERIZE_VERSION/dockerize-linux-amd64-$DOCKERIZE_VERSION.tar.gz \
    && tar -C /usr/local/bin -xzvf dockerize-linux-amd64-$DOCKERIZE_VERSION.tar.gz \
    && rm dockerize-linux-amd64-$DOCKERIZE_VERSION.tar.gz

# Копирование собранного JAR файла из локальной машины
COPY Quest_project-1.0.jar /app/Quest_project-1.0.jar

# Запуск приложения с использованием Dockerize для ожидания базы данных
ENTRYPOINT ["dockerize", "-wait", "tcp://quest-postgres-db:5432", "-timeout", "60s", "java", "-jar", "Quest_project-1.0.jar"]
