#!/usr/bin/env bash
set -x  # Enable debugging mode

# Load environment variables from .env file
source .env

# Define the path to the script directory
SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"

# Use variables from .env
JAR_PATH="$SCRIPT_DIR/$JAR_NAME"

# Function to clean up existing containers and images
cleanup_existing_resources() {
    echo "Checking for existing containers and images..."

    # Check if the container exists and remove it
    if docker ps -a --format '{{.Names}}' | grep -Eq "^${APP_TEST_CONTAINER_NAME}\$"; then
        echo "Container $APP_TEST_CONTAINER_NAME found. Removing..."
        if ! docker rm -f "$APP_TEST_CONTAINER_NAME"; then
            echo "Failed to remove container $APP_TEST_CONTAINER_NAME. Skipping this step."
            return 1  # Exit only the function with an error code
        fi
    else
        echo "Container $APP_TEST_CONTAINER_NAME not found. Skipping..."
    fi

    # Check if the image exists and remove it
    if docker images -q "$BUILDER_IMAGE_NAME" > /dev/null 2>&1; then
        echo "Image $BUILDER_IMAGE_NAME found. Removing..."
        if ! docker rmi "$BUILDER_IMAGE_NAME"; then
            echo "Failed to remove image $BUILDER_IMAGE_NAME. Skipping this step."
            return 1  # Exit only the function with an error code
        fi
    else
        echo "Image $BUILDER_IMAGE_NAME not found. Skipping..."
    fi

    return 0  # Indicate successful execution of the function
}

# Perform cleanup before building
cleanup_existing_resources

# Build the image with the test environment
echo "Building the image with the test environment..."
docker build -t "$BUILDER_IMAGE_NAME" -f "$SCRIPT_DIR/$DOCKERFILE_BUILD" .

# Determine the environment: Windows or Linux
if [[ "$OSTYPE" == "msys" || "$OSTYPE" == "win32" ]]; then
    echo "Running in Windows environment. Configuring for Docker."
    DOCKER_SOCKET="//var/run/docker.sock"
    HOST_OVERRIDE="host.docker.internal"
else
    echo "Running in Linux environment. Configuring for Docker."
    DOCKER_SOCKET="/var/run/docker.sock"
    HOST_OVERRIDE="172.17.0.1" # Explicitly setting the IP address of the Docker network
fi

# Trap to remove resources on error
trap 'echo "An error occurred. Cleaning up intermediate data..."; \
      docker rm -f "$APP_TEST_CONTAINER_NAME" 2>/dev/null || true; \
      docker rmi "$BUILDER_IMAGE_NAME" 2>/dev/null || true; \
      rm -f "$JAR_PATH" 2>/dev/null || true; \
      exit 1' ERR

# Run the container for testing and building
echo "Starting the test container with Testcontainers..."
docker run --name "$APP_TEST_CONTAINER_NAME" \
    -v "$DOCKER_SOCKET:/var/run/docker.sock" \
    -e TESTCONTAINERS_HOST_OVERRIDE="$HOST_OVERRIDE" \
    "$BUILDER_IMAGE_NAME"

# Check the exit status of the container
EXIT_CODE=$(docker inspect "$APP_TEST_CONTAINER_NAME" --format='{{.State.ExitCode}}')
echo "Container has finished. Exit code: $EXIT_CODE"
echo "Container logs after completion:"
docker logs "$APP_TEST_CONTAINER_NAME"

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

# Remove the intermediate builder image
if docker images -q "$BUILDER_IMAGE_NAME" > /dev/null 2>&1; then
    echo "Removing intermediate image: $BUILDER_IMAGE_NAME"
    docker rmi "$BUILDER_IMAGE_NAME" || {
        echo "Failed to remove intermediate image $BUILDER_IMAGE_NAME. Skipping..."
    }
else
    echo "Intermediate image $BUILDER_IMAGE_NAME not found. Skipping removal."
fi

echo "Build and test process completed successfully."