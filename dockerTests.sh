#!/usr/bin/env bash
set -x  # Включение режима отладки

# Определение пути к директории скрипта
SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"

# Имя JAR-файла, который генерируется в процессе сборки
JAR_NAME="Quest_project-1.0.jar"
JAR_PATH="$SCRIPT_DIR/$JAR_NAME"

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
    HOST_OVERRIDE="localhost"
fi

# Запуск контейнера для тестов и сборки
echo "Запуск контейнера для тестов с Testcontainers..."
docker run --name my-app-container \
    -v "$DOCKER_SOCKET:/var/run/docker.sock" \
    -e TESTCONTAINERS_HOST_OVERRIDE="$HOST_OVERRIDE" \
    my-app-builder &

# Ожидание, пока контейнер перейдет в состояние "running"
echo "Ожидание запуска контейнера..."
until [ "$(docker inspect --format='{{.State.Status}}' my-app-container 2>/dev/null)" == "running" ]; do
    echo "Контейнер еще не запущен, ожидаем..."
    sleep 2
done

# Цикл проверки состояния контейнера с помощью docker inspect
echo "Контейнер запущен, проверка состояния каждые 2 секунды..."
while true; do
    STATUS=$(docker inspect --format='{{.State.Status}}' my-app-container)
    if [[ "$STATUS" == "exited" ]]; then
        echo "Контейнер завершил работу."
        break
    elif [[ "$STATUS" == "running" ]]; then
        echo "Контейнер еще работает, ожидаем..."
    else
        echo "Контейнер в неожиданном состоянии или не найден: $STATUS"
        exit 1
    fi
    sleep 2
done

# Проверка статуса завершения контейнера
EXIT_CODE=$(docker inspect my-app-container --format='{{.State.ExitCode}}')
echo "Контейнер завершил работу. Код завершения: $EXIT_CODE"

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

# Вывод логов контейнера для проверки статуса
echo "Логи контейнера:"
docker logs my-app-container

# Удаление контейнера после копирования файла
echo "Удаление контейнера..."
docker rm my-app-container

# Сборка финального образа
echo "Сборка финального образа..."
docker build -t my-app-final -f "$SCRIPT_DIR/Docker-Final.Dockerfile" .

# Удаление JAR-файла после сборки финального образа
rm "$JAR_PATH"
echo "Удален временный JAR-файл: $JAR_PATH"