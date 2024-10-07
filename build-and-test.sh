#!/usr/bin/env bash
set -x  # Enable debugging mode

# Load environment variables from .env file
source .env

# Define the path to the script directory
SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
JAR_PATH="$SCRIPT_DIR/$JAR_NAME"

# Clean the project before building
echo "Cleaning the project..."
mvn clean

# Build the image with the test environment
echo "Building the image with the test environment..."
docker build -t "$BUILDER_IMAGE_NAME" -f "$SCRIPT_DIR/$DOCKERFILE_BUILD" .

# Run the container for testing and building
echo "Starting the test container with Testcontainers..."
docker run --name "$APP_TEST_CONTAINER_NAME" \
    -v /var/run/docker.sock:/var/run/docker.sock \
    "$BUILDER_IMAGE_NAME"

# Check the exit status of the container
EXIT_CODE=$(docker inspect "$APP_TEST_CONTAINER_NAME" --format='{{.State.ExitCode}}')
echo "Container has finished. Exit code: $EXIT_CODE"

# Check if the container finished successfully
if [ "$EXIT_CODE" -ne 0 ]; then
    echo "Container exited with an error (code $EXIT_CODE). Check the logs:"
    docker logs "$APP_TEST_CONTAINER_NAME"
    exit 1
fi

# Check and copy the JAR file from the stopped container
if docker cp "$APP_TEST_CONTAINER_NAME":/app/target/"$JAR_NAME" "$JAR_PATH"; then
    echo "JAR file copied successfully."
else
    echo "Failed to copy the JAR file. Check the container logs:"
    docker logs "$APP_TEST_CONTAINER_NAME"
    exit 1
fi

# Remove the container after copying the file
echo "Removing the container..."
docker rm "$APP_TEST_CONTAINER_NAME"

echo "Build and test completed successfully."