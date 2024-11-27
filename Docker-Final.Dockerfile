# Create the final Docker image based on lightweight Amazon Corretto
FROM amazoncorretto:21-alpine-full

# Set up the working directory
WORKDIR /app

# Copy templates and images
COPY src/main/webapp/WEB-INF/images /app/images

# Install wget
RUN apk update && \
    apk add --no-cache wget && \
    rm -rf /var/lib/apt/lists/*

# Install Dockerize to wait for external services
ENV DOCKERIZE_VERSION=v0.6.1
RUN wget https://github.com/jwilder/dockerize/releases/download/$DOCKERIZE_VERSION/dockerize-linux-amd64-$DOCKERIZE_VERSION.tar.gz \
    && tar -C /usr/local/bin -xzvf dockerize-linux-amd64-$DOCKERIZE_VERSION.tar.gz \
    && rm dockerize-linux-amd64-$DOCKERIZE_VERSION.tar.gz

# Ensure the images directory has write permissions
RUN chmod -R 777 /app/images

# Define build arguments
ARG JAR_NAME
ARG DATABASE_CONTAINER_NAME

# Set environment variables using the values of ARGs
ENV JAR_NAME=${JAR_NAME}
ENV DATABASE_CONTAINER_NAME=${DATABASE_CONTAINER_NAME}

# Copy the built JAR file from the local machine
COPY ${JAR_NAME} /app/${JAR_NAME}

# Run the application using Dockerize to wait for the database
# Use sh -c to ensure variable substitution works correctly
ENTRYPOINT sh -c "dockerize -wait tcp://\$DATABASE_CONTAINER_NAME:5432 -timeout 60s java -jar /app/\$JAR_NAME"