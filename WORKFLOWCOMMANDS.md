
# Stop running docker containers
docker compose down

# Clearing docker caches
docker builder prune -a

# Building docker container from scratch
docker compose up --build

# Find running docker containers
docker ps

# To run Maven tests:
docker compose run --rm java_test