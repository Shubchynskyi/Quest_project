# Create an intermediate image with Maven and Java 21
FROM maven:3.9.6-eclipse-temurin-21 AS build

# Set up the working directory
WORKDIR /app

# Copy project files
COPY pom.xml .
COPY src ./src

# Copy templates and images
COPY src/main/resources/templates /app/templates
COPY src/main/webapp/WEB-INF/images /app/images

# Download Maven dependencies
RUN mvn dependency:go-offline

# Run tests and build the JAR
CMD ["mvn", "verify"]