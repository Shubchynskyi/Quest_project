#!/bin/bash
cd "$(dirname "$0")/.." || exit #if script not into root directory
mvn clean install -DskipTests

echo "Starting docker-compose"
docker-compose up -d
echo "Docker-compose started"
docker-compose logs -f