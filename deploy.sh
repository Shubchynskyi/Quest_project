#!/usr/bin/env bash
set -x  # Enable debugging mode

# Load environment variables from .env file
source .env

# Define the path to the script directory
SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"

# Use variables from .env
JAR_PATH="$SCRIPT_DIR/$JAR_NAME"

# Use the first argument as the compose file, or default to COMPOSE_FILE from .env
COMPOSE_FILE_ARG=${1:-$COMPOSE_FILE}

# Define paths dynamically
PROJECT_IMAGES_DIR="$SCRIPT_DIR/$PROJECT_USERS_IMAGES_DIR"
CONTAINER_IMAGES_DIR="$APP_DIRECTORIES_IMAGES"

# Build the final image with the new name
echo "Building the final image..."
docker build \
  --build-arg JAR_NAME="$JAR_NAME" \
  --build-arg DATABASE_CONTAINER_NAME="$DATABASE_CONTAINER_NAME" \
  -t "$FINAL_IMAGE_NAME" \
  -f "$SCRIPT_DIR/$DOCKERFILE_FINAL" .

# Remove the temporary JAR file
rm "$JAR_PATH"
echo "Removed the temporary JAR file: $JAR_PATH"

# Run Docker Compose to deploy the application and the database
echo "Starting Docker Compose with file: $COMPOSE_FILE_ARG"
docker-compose -f "$COMPOSE_FILE_ARG" up -d

# Wait for the application container to be ready
echo "Waiting for the application container to be ready..."
until [ "$(docker inspect -f '{{.State.Running}}' "$FINAL_APP_CONTAINER_NAME")" == "true" ]; do
    sleep 1
    echo "Waiting for $FINAL_APP_CONTAINER_NAME..."
done
echo "$FINAL_APP_CONTAINER_NAME is ready!"

# Copy default images from project to container's images directory
if [ -d "$PROJECT_IMAGES_DIR" ]; then
    echo "Copying default images to container's images directory..."
    docker cp "$PROJECT_IMAGES_DIR/." "$FINAL_APP_CONTAINER_NAME:$CONTAINER_IMAGES_DIR"
else
    echo "Error: Project images directory does not exist: $PROJECT_IMAGES_DIR"
    exit 1
fi

echo "Script completed, application started via Docker Compose."