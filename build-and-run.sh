#!/usr/bin/env bash
set -x  # Включение режима отладки

# Определение пути к директории скрипта
SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"

# Имя JAR-файла, который генерируется в процессе сборки
JAR_NAME="Quest_project-1.0.jar"
JAR_PATH="$SCRIPT_DIR/$JAR_NAME"
FINAL_IMAGE_NAME="quests-app"

# Очистка проекта перед сборкой
echo "Очистка проекта..."
mvn clean

# Сборка образа с тестовой средой
echo "Сборка образа с тестовой средой..."
docker build -t my-app-builder -f "$SCRIPT_DIR/Docker-Build.Dockerfile" .

# Определение среды: Windows или Linux
if [[ "$OSTYPE" == "msys" || "$OSTYPE" == "win32" ]]; then
    echo "Работаем в Windows среде. Настройка для Docker."
    DOCKER_SOCKET="//var/run/docker.sock"
    HOST_OVERRIDE="host.docker.internal"
else
    echo "Работаем в Linux среде. Настройка для Docker."
    DOCKER_SOCKET="/var/run/docker.sock"
    HOST_OVERRIDE="172.17.0.1" # Явно указываем IP адрес Docker сети
fi

# Запуск контейнера для тестов и сборки
echo "Запуск контейнера для тестов с Testcontainers..."
docker run --name my-app-container \
    -v "$DOCKER_SOCKET:/var/run/docker.sock" \
    -e TESTCONTAINERS_HOST_OVERRIDE="$HOST_OVERRIDE" \
    my-app-builder

# Ловушка для удаления ресурсов при ошибке
trap 'echo "Удаление промежуточных данных при ошибке..."; \
      docker rm -f my-app-container 2>/dev/null || true; \
      docker rmi my-app-builder 2>/dev/null || true; \
      rm -f "$JAR_PATH" 2>/dev/null || true; \
      exit 1' ERR

# Проверка статуса завершения контейнера
EXIT_CODE=$(docker inspect my-app-container --format='{{.State.ExitCode}}')
echo "Контейнер завершил работу. Код завершения: $EXIT_CODE"
echo "Логи контейнера после завершения:"
docker logs my-app-container

# Проверка на успешное завершение работы контейнера
if [ "$EXIT_CODE" -ne 0 ]; then
    echo "Контейнер завершился с ошибкой (код $EXIT_CODE). Проверьте логи:"
    docker logs my-app-container
    exit 1
fi

# Проверка и копирование JAR-файла из остановленного контейнера
if docker cp my-app-container:/app/target/$JAR_NAME "$JAR_PATH"; then
    echo "JAR-файл успешно скопирован."
else
    echo "Не удалось скопировать JAR-файл. Проверьте логи контейнера:"
    docker logs my-app-container
    exit 1
fi

# Удаление контейнера после копирования файла
echo "Удаление контейнера..."
docker rm my-app-container

# Удаление образа сборки, так как он больше не нужен
echo "Удаление промежуточного образа..."
docker rmi my-app-builder

# Сборка финального образа с новым именем
echo "Сборка финального образа..."
docker build -t $FINAL_IMAGE_NAME -f "$SCRIPT_DIR/Docker-Final.Dockerfile" .

# Удаление временного JAR-файла
rm "$JAR_PATH"
echo "Удален временный JAR-файл: $JAR_PATH"

# Запуск Docker Compose для развертывания приложения и базы данных с использованием локального файла
echo "Запуск Docker Compose..."
docker-compose -f docker-compose-local.yaml up -d

echo "Скрипт завершен, приложение запущено через Docker Compose."
