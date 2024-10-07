#!/usr/bin/env bash
set -x  # Enable debugging mode

# Load environment variables from .env file
source .env

# Define the path to the script directory
SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"

# Use variables from .env
JAR_PATH="$SCRIPT_DIR/$JAR_NAME"

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
echo "Starting Docker Compose..."
docker-compose -f "$COMPOSE_FILE" up -d

echo "Script completed, application started via Docker Compose."