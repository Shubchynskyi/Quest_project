#!/usr/bin/env bash
set -x  # Enable debugging mode

# Load environment variables from .env file
source .env

echo "Starting Docker cleanup..."

# Define the containers to be removed based on the settings
ALL_CONTAINERS=("$APP_TEST_CONTAINER_NAME" "$DATABASE_CONTAINER_NAME" "$FINAL_APP_CONTAINER_NAME")

# Remove all containers related to the application
for CONTAINER in "${ALL_CONTAINERS[@]}"; do
  if docker ps -a | grep -q "$CONTAINER"; then
    echo "Removing container $CONTAINER..."
    docker rm -f "$CONTAINER" || true
  fi
done

# Define the images to be removed based on the settings
ALL_IMAGES=("$BUILDER_IMAGE_NAME" "$FINAL_IMAGE_NAME")

# Remove all images related to the application
for IMAGE in "${ALL_IMAGES[@]}"; do
  if docker images | grep -q "$IMAGE"; then
    echo "Removing image $IMAGE..."
    docker rmi -f "$IMAGE" || true
  fi
done

# Remove the JAR file if it exists
if [[ -f "$JAR_PATH" ]]; then
  echo "Removing JAR file: $JAR_PATH..."
  rm -f "$JAR_PATH" || true
fi

echo "Cleanup completed."