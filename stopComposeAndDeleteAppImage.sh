#!/bin/bash
#cd "$(dirname "$0")/.." || exit
#docker-compose down --rmi 'local' # NO volume
docker-compose down --rmi 'local' --volumes   # +volume