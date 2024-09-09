#!/usr/bin/env bash
set -x  # Включение режима отладки

echo "Начало очистки Docker..."

# Удаление контейнеров, созданных приложением (без базы данных)
APP_CONTAINERS=("my-app-container" "quests-app")

for CONTAINER in "${APP_CONTAINERS[@]}"; do
  if docker ps -a | grep -q "$CONTAINER"; then
    echo "Удаление контейнера $CONTAINER..."
    docker rm -f "$CONTAINER" || true
  fi
done

# Удаление конкретных образов, созданных приложением
APP_IMAGES=("my-app-builder" "quests-app")

for IMAGE in "${APP_IMAGES[@]}"; do
  if docker images | grep -q "$IMAGE"; then
    echo "Удаление образа $IMAGE..."
    docker rmi -f "$IMAGE" || true
  fi
done

echo "Очистка завершена."
