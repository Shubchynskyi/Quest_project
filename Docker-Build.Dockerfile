# Этап 1: Создание промежуточного образа с Docker, Maven и Java 17
FROM docker:24.0.5 AS build

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

# Запуск тестов и сборка JAR
CMD ["mvn", "verify"]